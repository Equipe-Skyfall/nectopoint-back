package com.nectopoint.backend.controllers.user;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nectopoint.backend.dtos.LoginRequestDTO;
import com.nectopoint.backend.services.AuthorizationService;

import javax.naming.AuthenticationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/auth")
public class UserAuthController {
    

    @Autowired
    private AuthorizationService authorizationService;

    @PostMapping("/usuario")
    public String create(@RequestBody LoginRequestDTO loginRequestDTO) throws AuthenticationException {
    
        return this.authorizationService.execute(loginRequestDTO);
    }
    

}
