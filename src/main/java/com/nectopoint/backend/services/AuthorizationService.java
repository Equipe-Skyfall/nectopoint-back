package com.nectopoint.backend.services;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;

import javax.naming.AuthenticationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.nectopoint.backend.dtos.LoginRequestDTO;
import com.nectopoint.backend.dtos.LoginResponseDTO;
import com.nectopoint.backend.repositories.UserRepository;

@Service
public class AuthorizationService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${security.token}")
    private String secretKey;


    public LoginResponseDTO execute(LoginRequestDTO loginRequestDTO)throws AuthenticationException{
        var user = this.userRepository.findByCpf(loginRequestDTO.getCpf()).orElseThrow(() ->{
            throw new UsernameNotFoundException("usuário ou senha incorretos");
        });
        

        System.out.println(user);
        // Verifica se as senhas do usuário são iguais, se sim retorna um token
        var passwordMatch =  this.passwordEncoder.matches(loginRequestDTO.getPassword(), user.getPassword());

        //Tratamento de erro caso as senhas não sejam iguais
        if(!passwordMatch){
            throw new AuthenticationException(); //403 Forbidden
        }
    
        System.out.println("cargo" + user.getTitle().toString());
        //criação do token
        Algorithm algorithm = Algorithm.HMAC256(secretKey);
       var token = JWT.create()
                    .withExpiresAt(Instant.now().plus(Duration.ofHours(2)))
                    .withIssuer("Nectopoint")
                    .withSubject(user.getId().toString())
                    .withClaim("roles",user.getTitle().toString())
                    .sign(algorithm);
    
        var userWithAccessToken = LoginResponseDTO.builder()
                                                .access_token(token)
                                                .build();
        
         return (LoginResponseDTO) userWithAccessToken;

      }
}

