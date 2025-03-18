package com.nectopoint.backend.security;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.nectopoint.backend.providers.JWTProvider;
import java.util.List;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class SecurityUserFilter extends OncePerRequestFilter {

    @Autowired
    private  JWTProvider jwtProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

                SecurityContextHolder.getContext().setAuthentication(null);
                String header = request.getHeader("Authorization");

                if(request.getRequestURI().startsWith("/usuario")){

                    if(header != null && header.startsWith("Bearer ")){
                        // String headers = header.substring(7);
                        DecodedJWT token = this.jwtProvider.validateToken(header);
                        if(token == null){
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            return;
                        }
                        request.setAttribute("user_id", token);
                        // UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(subjectToken, null,Collections.emptyList());
                        // SecurityContextHolder.getContext().setAuthentication(auth);
                        
                        // Get user ID from token
            String userId = token.getSubject();
            
            // Get roles from token
            String roles = token.getClaim("roles").asString();
            
            // Create authorities
            List<SimpleGrantedAuthority> authorities = new ArrayList<>();
            authorities.add(new SimpleGrantedAuthority("ROLE_" + roles));
            
            // Set authentication in security context
            UsernamePasswordAuthenticationToken authentication = 
                new UsernamePasswordAuthenticationToken(userId, null, authorities);
            
            SecurityContextHolder.getContext().setAuthentication(authentication);

                        System.out.println("================================================TOKEN");
                        System.out.println(header);
                    }
                }
     
                filterChain.doFilter(request,response);
    }
    
}
