package com.nectopoint.backend.security;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.nectopoint.backend.providers.JWTProvider;
import com.auth0.jwt.interfaces.DecodedJWT;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class SecurityUserFilter extends OncePerRequestFilter {

    @Autowired
    private JWTProvider jwtProvider;
    
    private static final String COOKIE_NAME = "jwt_token";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        SecurityContextHolder.getContext().setAuthentication(null);
        
        if (request.getRequestURI().startsWith("/usuario") && !request.getRequestURI().equals("/usuario/auth")) {
            String token = null;
            
            // Pegando o token do cookie
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if (COOKIE_NAME.equals(cookie.getName())) {
                        token = cookie.getValue();
                        break;
                    }
                }
            }
            
            // Caso queira usar o Bearer token no header
            // if (token == null) {
            //     String header = request.getHeader("Authorization");
            //     if (header != null && header.startsWith("Bearer ")) {
            //         token = header;
            //     }
            // } else {
            //     // Add Bearer prefix if token is from cookie
            //     token = "Bearer " + token;
            // }
            
            if (token != null) {
                DecodedJWT decodedToken = this.jwtProvider.validateToken(token);
                if (decodedToken == null) {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    return;
                }
                
                request.setAttribute("user_id", decodedToken);
                
                // Get user ID from token
                String userId = decodedToken.getSubject();
                
                // Get roles from token
                String roles = decodedToken.getClaim("roles").asString();
                
                // Create authorities
                List<SimpleGrantedAuthority> authorities = new ArrayList<>();
                authorities.add(new SimpleGrantedAuthority("ROLE_" + roles));
                
                // Set authentication in security context
                UsernamePasswordAuthenticationToken authentication = 
                    new UsernamePasswordAuthenticationToken(userId, null, authorities);
                
                SecurityContextHolder.getContext().setAuthentication(authentication);
                
                System.out.println("================================================TOKEN");
                System.out.println(token);
            }
        }
 
        filterChain.doFilter(request, response);
    }
}