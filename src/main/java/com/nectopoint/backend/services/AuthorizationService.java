package com.nectopoint.backend.services;

import java.time.Duration;
import java.time.Instant;

import javax.naming.AuthenticationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.nectopoint.backend.dtos.LoginRequestDTO;
import com.nectopoint.backend.enums.TipoStatusUsuario;
import com.nectopoint.backend.providers.JWTProvider;
import com.nectopoint.backend.repositories.UserRepository;
import com.nectopoint.backend.repositories.userSession.UserSessionRepository;
import com.nectopoint.backend.repositories.userSession.UserSessionRepositoryCustom;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

@Service
public class AuthorizationService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserSessionRepository userSessionRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private JWTProvider jwtProvider;

    @Value("${security.token}")
    private String secretKey;
    
    // Cookie configuration
    private static final String COOKIE_NAME = "jwt_token";
    private static final int COOKIE_MAX_AGE = 7200; // 2 hours in seconds
    private static final String COOKIE_PATH = "/";

    //Valida usuário e senha
    public String validateCredentials(LoginRequestDTO loginRequestDTO) throws AuthenticationException {
        var user = this.userRepository.findByCpf(loginRequestDTO.getCpf()).orElseThrow(() -> {
            throw new UsernameNotFoundException("usuário ou senha incorretos");
        });
        
        // Verifica se as senhas do usuário são iguais, se sim retorna um token
        var passwordMatch = (boolean) this.passwordEncoder.matches(loginRequestDTO.getPassword(), user.getPassword());
        // Tratamento de erro caso as senhas não sejam iguais
        if (!passwordMatch) {
            throw new AuthenticationException(); // 403 Forbidden
        }
        
        return user.getId().toString();
    }
    
    // Gera e Retorna o token após o usuário ser verificado
    public void generateAndSetToken(String userId, HttpServletResponse response) {
        var user = this.userRepository.findById(Long.parseLong(userId))
            .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));
        
        var userSession = this.userSessionRepository.findByColaborador(Long.parseLong(userId));
        TipoStatusUsuario status = userSession.getDados_usuario().getStatus();
        System.out.println(status.toString());
        // Generate token using JWTProvider
        String token = jwtProvider.generateToken(userId, user.getTitle().toString(),status.toString());
    
        // Add token to cookie
        Cookie cookie = new Cookie(COOKIE_NAME, token);
        cookie.setHttpOnly(true); // Prevents JavaScript access
        cookie.setSecure(false); 
        cookie.setPath(COOKIE_PATH);
        cookie.setMaxAge(COOKIE_MAX_AGE);
        cookie.setAttribute("SameSite", "Lax");
        
        // Add cookie to response
        response.addCookie(cookie);
    }

    
    public String execute(LoginRequestDTO loginRequestDTO, HttpServletResponse response) throws AuthenticationException {
        var userId = validateCredentials(loginRequestDTO);
        generateAndSetToken(userId, response);
        return userId;
    }
}