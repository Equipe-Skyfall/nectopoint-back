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
import com.nectopoint.backend.repositories.UserRepository;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

@Service
public class AuthorizationService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${security.token}")
    private String secretKey;
    
    // Cookie configuration
    private static final String COOKIE_NAME = "jwt_token";
    private static final int COOKIE_MAX_AGE = 7200; // 2 hours in seconds
    private static final String COOKIE_PATH = "/";

    //  LoginResponseDTO casa queira mandar o Bearer token sem ser por cookies 
    public String execute(LoginRequestDTO loginRequestDTO, HttpServletResponse response) throws AuthenticationException {
        var user = this.userRepository.findByCpf(loginRequestDTO.getCpf()).orElseThrow(() -> {
            throw new UsernameNotFoundException("usuário ou senha incorretos");
        });
        
        // Verifica se as senhas do usuário são iguais, se sim retorna um token
        var passwordMatch = (boolean) this.passwordEncoder.matches(loginRequestDTO.getPassword(), user.getPassword());
        // Tratamento de erro caso as senhas não sejam iguais
        if (!passwordMatch) {
            throw new AuthenticationException(); // 403 Forbidden
        }
    
        // Criação do token
        Algorithm algorithm = Algorithm.HMAC256(secretKey);
        var token = JWT.create()
                    .withExpiresAt(Instant.now().plus(Duration.ofHours(2)))
                    .withIssuer("Nectopoint")
                    .withSubject(user.getId().toString())
                    .withClaim("roles", user.getTitle().toString())
                    .sign(algorithm);
    
        // Add token to cookie
        Cookie cookie = new Cookie(COOKIE_NAME, token);
        cookie.setHttpOnly(true); // Prevents JavaScript access
        cookie.setSecure(false); 
        cookie.setPath(COOKIE_PATH);
        cookie.setMaxAge(COOKIE_MAX_AGE);
        cookie.setAttribute("SameSite", "Lax");
        
        // Add cookie to response
        response.addCookie(cookie);
        
        // Still returning token in response body for backward compatibility
        // You can remove this if you want to rely only on cookies
        // var userWithAccessToken = LoginResponseDTO.builder()
        //                                        .access_token(token)
        //                                        .build();
        
        return user.getId().toString();
    }
}