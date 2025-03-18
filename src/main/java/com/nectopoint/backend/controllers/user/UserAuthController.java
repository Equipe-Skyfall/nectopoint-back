package com.nectopoint.backend.controllers.user;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nectopoint.backend.dtos.LoginRequestDTO;
import com.nectopoint.backend.services.AuthorizationService;

import javax.naming.AuthenticationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/usuario")
public class UserAuthController {
    

    @Autowired
    private AuthorizationService authorizationService;

    @PostMapping("/auth")
    public ResponseEntity<Object> create(@RequestBody LoginRequestDTO loginRequestDTO)  {
        try{

           var result = this.authorizationService.execute(loginRequestDTO);
            return ResponseEntity.ok().body(result);
        }catch(Exception e){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }
    

}
