package com.nectopoint.backend.controllers.registry;

import java.time.Instant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    @PostMapping("/bater-ponto/{id_colaborador}")
    public PointRegistryEntity postPunch(@PathVariable Long id_colaborador) {
        System.out.println("Received ID: " + id_colaborador);
        return registryService.postPunch(id_colaborador);
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
        @RequestParam(required = false) TipoStatusTurno statusTurno,
        @RequestParam Long id_colaborador
    ) {
        Pageable pageable = PageRequest.of(page, size);

        Page<PointRegistryEntity> pointRegistryPage = registryRepo.findByParamsDynamic(id_colaborador, startDate, endDate, statusTurno, pageable);

        return new ResponseEntity<>(pointRegistryPage, HttpStatus.OK);
    }
    
}
