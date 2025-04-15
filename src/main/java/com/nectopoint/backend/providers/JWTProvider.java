package com.nectopoint.backend.providers;

import java.time.Duration;
import java.time.Instant;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;

@Service
public class JWTProvider {
    @Value("${security.token}")
    private String secretKey;

    public DecodedJWT validateToken(String token) {
        // Para usar com o token no header
        // if (token.startsWith("Bearer ")) {
        //     token = token.replace("Bearer ", "");
        // }
        
        Algorithm algorithm = Algorithm.HMAC256(secretKey);
       
        try {
            var decodedJWT = JWT.require(algorithm)
                .withIssuer("Nectopoint")
                .build()
                .verify(token);
            System.out.println("✅ Token successfully validated! User ID: " + decodedJWT.getSubject());
            return decodedJWT;
        } catch (JWTVerificationException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public String generateToken(String userId, String role, String status) {
        Algorithm algorithm = Algorithm.HMAC256(secretKey);
        return JWT.create()
                .withExpiresAt(Instant.now().plus(Duration.ofHours(2)))
                .withIssuer("Nectopoint")
                .withSubject(userId)
                .withClaim("roles", role)
                .withClaim("status", status)
                .sign(algorithm);
    }
}