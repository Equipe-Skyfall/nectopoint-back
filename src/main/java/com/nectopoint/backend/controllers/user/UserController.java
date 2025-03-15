package com.nectopoint.backend.controllers.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nectopoint.backend.modules.user.UserEntity;
import com.nectopoint.backend.repositories.UserRepository;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/usuario")
public class UserController {
    
    @Autowired
    private UserRepository userRepository;


    @PostMapping("/")
    public UserEntity create(@Valid @RequestBody UserEntity userEntity){
        return this.userRepository.save(userEntity);
    }

}
