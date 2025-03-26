package com.nectopoint.backend.controllers.user;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nectopoint.backend.dtos.LoginRequestDTO;
import com.nectopoint.backend.repositories.userSession.UserSessionRepository;
import com.nectopoint.backend.services.AuthorizationService;

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

    @PostMapping("/auth")
    public ResponseEntity<Object> create(@RequestBody LoginRequestDTO loginRequestDTO, HttpServletResponse response) {
        try {
            // Recebe o userId(str) depois de realizar a autorização
            var userIdStr = this.authorizationService.execute(loginRequestDTO, response);
            // converte para Long para ajustar o Tipo
            var userId = Long.parseLong(userIdStr);
            // busca o usuário na sessão do mongoDb
            var userInfo = this.userSessionRepo.findByColaborador(userId);
            
            System.out.println(userInfo);
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