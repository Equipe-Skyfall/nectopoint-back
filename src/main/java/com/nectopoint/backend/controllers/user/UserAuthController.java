package com.nectopoint.backend.controllers.user;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nectopoint.backend.dtos.LoginRequestDTO;
import com.nectopoint.backend.repositories.UserSessionRepository;
import com.nectopoint.backend.services.AuthorizationService;

import javax.naming.AuthenticationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

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
}