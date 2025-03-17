package com.nectopoint.backend.controllers.user;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nectopoint.backend.modules.user.UserEntity;
import com.nectopoint.backend.repositories.UserRepository;

import lombok.Data;
@Data
@RestController
@RequestMapping("/login")
public class LoginController {

    @Autowired
    private UserRepository userRepository;

    @PostMapping
public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
    String cpf = loginRequest.getCpf().replaceAll("\\D", ""); 
    String password = loginRequest.getPassword();

    Optional<UserEntity> optionalUser = userRepository.findByCpf(cpf);

    if (optionalUser.isPresent()) {
        UserEntity user = optionalUser.get();
        if (user.getPassword().equals(password)) { 
            return ResponseEntity.ok(Map.of("message", "Login bem-sucedido", "role", user.getTitle()));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("CPF ou senha incorretos");
        }
    } else {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuário não encontrado");
    }
}
}