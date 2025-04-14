package com.nectopoint.backend.controllers.user;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nectopoint.backend.dtos.UserSessionDTO;
import com.nectopoint.backend.enums.TipoStatusUsuario;
import com.nectopoint.backend.modules.user.UserSessionEntity;
import com.nectopoint.backend.repositories.userSession.UserSessionRepository;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/sessao/usuario")
public class UserSessionController {

    @Autowired
    private UserSessionRepository userSessionRepo;

    @GetMapping("/todos")
    public ResponseEntity<Page<UserSessionDTO>> getUsersList(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "5") int size,
        @RequestParam(required = false) String cpf,
        @RequestParam(required = false) List<TipoStatusUsuario> lista_status
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<UserSessionDTO> userSessionPage;

        userSessionPage = userSessionRepo.findByParamsDynamic(cpf, lista_status, pageable);

        return new ResponseEntity<>(userSessionPage, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserSessionEntity> getMethodName(@PathVariable Long id) {
        return ResponseEntity.ok().body(userSessionRepo.findByColaborador(id));
    }
    

    @GetMapping("/me")
    public ResponseEntity<UserSessionEntity> getUserSession() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication.getPrincipal() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Long id_colaborador = Long.parseLong(authentication.getPrincipal().toString());
        UserSessionEntity userSession = userSessionRepo.findByColaborador(id_colaborador);
        
        if (userSession == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        return ResponseEntity.ok().body(userSession);
    }

}
