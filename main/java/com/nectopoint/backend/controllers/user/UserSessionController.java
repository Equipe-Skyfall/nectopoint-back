package com.nectopoint.backend.controllers.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nectopoint.backend.modules.user.UserSessionEntity;
import com.nectopoint.backend.repositories.UserSessionRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/sessao/usuario")
public class UserSessionController {

    @Autowired
    private UserSessionRepository userSessionRepo;

    @GetMapping("/")
    public ResponseEntity<Page<UserSessionEntity>> getMethodName(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "5") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<UserSessionEntity> userSessionPage;

        userSessionPage = userSessionRepo.findAll(pageable);

        return new ResponseEntity<>(userSessionPage, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public UserSessionEntity getUserSession(@PathVariable Long id) {
        return userSessionRepo.findByColaborador(id);
    }

}
