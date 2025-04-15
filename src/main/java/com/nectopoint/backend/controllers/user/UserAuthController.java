package com.nectopoint.backend.controllers.user;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nectopoint.backend.dtos.LoginRequestDTO;
import com.nectopoint.backend.dtos.VerifyCodeRequestDTO;
import com.nectopoint.backend.repositories.UserRepository;
import com.nectopoint.backend.repositories.userSession.UserSessionRepository;
import com.nectopoint.backend.services.AuthorizationService;
import com.nectopoint.backend.services.EmailService;
import com.nectopoint.backend.services.VerificationCodeService;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/usuario")
public class UserAuthController {
    
    @Autowired
    private AuthorizationService authorizationService;

    @Autowired
    private UserSessionRepository userSessionRepo;
    
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private VerificationCodeService verificationService;


    //Autenticação inicial cpf e senha -> gera o código de verificação e manda para o email
    @PostMapping("/auth")
    public ResponseEntity<Object> authenticate(@RequestBody LoginRequestDTO loginRequestDTO) {
        try {
            // valida as credenciais(cpf e senha)
            var userId = this.authorizationService.validateCredentials(loginRequestDTO);
            
            // pega o usuário do repositório
            var user = this.userRepository.findById(Long.parseLong(userId))
                .orElseThrow(() -> new Exception("Usuário não encontrado"));
            
            // gera o código de verificacão para o email
            String code = verificationService.generateCode(userId);
            
            // envia o código para o email do usuário
            emailService.sendVerificationCode(user.getEmail(), code);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Código de verificação enviado para seu email");
            response.put("userId", userId);
            
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }
    
    // Verifica se o código digitado está correto, ele verifica se o id do usuário bate com o código de verificação
    @PostMapping("/verify")
    public ResponseEntity<Object> verifyCode(@RequestBody VerifyCodeRequestDTO verifyRequest, 
                                       HttpServletResponse response) {
        try {
            // verifica o código
            boolean isValid = verificationService.verifyCode(
                verifyRequest.getUserId(), 
                verifyRequest.getVerificationCode()
            );
            
            if (!isValid) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Código de verificação inválido ou expirado"));
            }
            
            // gera e seta o token jwt
            this.authorizationService.generateAndSetToken(verifyRequest.getUserId(), response);
            
            // pega as informações do usuário para retornar na resposta
            var userInfo = this.userSessionRepo.findByColaborador(Long.parseLong(verifyRequest.getUserId()));
            
            return ResponseEntity.ok().body(userInfo);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<Object> logout(HttpServletResponse response) {
       
        Cookie cookie = new Cookie("jwt_token", null);
        cookie.setHttpOnly(true);
        cookie.setSecure(false); 
        cookie.setPath("/");
        cookie.setMaxAge(0); // Setting to 0 causes browser to delete the cookie
        
        response.addCookie(cookie);
        
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("message", "Logout successful");
        
        return ResponseEntity.ok().body(responseBody);
    }
}