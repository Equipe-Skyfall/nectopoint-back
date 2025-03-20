package com.nectopoint.backend.controllers.registry;

import java.time.Instant;

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

import com.nectopoint.backend.dtos.PointRegistryDTO;
import com.nectopoint.backend.enums.TipoStatusTurno;
import com.nectopoint.backend.modules.usersRegistry.PointRegistryEntity;
import com.nectopoint.backend.repositories.pointRegistry.PointRegistryRepository;
import com.nectopoint.backend.services.PointRegistryService;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;



@RestController
@RequestMapping("/turno")
public class PointRegistryController {
    
    @Autowired
    private PointRegistryRepository registryRepo;
    @Autowired
    private PointRegistryService registryService;

    @PostMapping("/bater-ponto")
    public ResponseEntity<PointRegistryEntity> postPunch() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication.getPrincipal() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Long id_colaborador = Long.parseLong(authentication.getPrincipal().toString());
        
        return ResponseEntity.ok(registryService.postPunch(id_colaborador));
    }

    @PostMapping("/bater-ponto/correcao")
    public void postPunchCorrection(@Valid @RequestBody PointRegistryDTO requestData) {
        registryService.correctPointPunch(requestData);
    }
    
    
    @GetMapping("/{id}")
    public PointRegistryEntity getPointById(@PathVariable String id) {
        return registryRepo.findById(id).get();
    }

    @GetMapping("/historico")
    public ResponseEntity<Page<PointRegistryEntity>> historicoTodos(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "5") int size,
        @RequestParam(required = false) Instant startDate,
        @RequestParam(required = false) Instant endDate,
        @RequestParam(required = false) TipoStatusTurno status_turno,
        @RequestParam(required = false) Long id_colaborador
    ) {
        Pageable pageable = PageRequest.of(page, size);

        Page<PointRegistryEntity> pointRegistryPage = registryRepo.findByParamsDynamic(id_colaborador, startDate, endDate, status_turno, pageable);

        return new ResponseEntity<>(pointRegistryPage, HttpStatus.OK);
    }
    
}
