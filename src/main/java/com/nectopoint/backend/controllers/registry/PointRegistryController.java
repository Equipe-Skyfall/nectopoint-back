package com.nectopoint.backend.controllers.registry;

import java.time.Instant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nectopoint.backend.dtos.PointRegistryDTO;
import com.nectopoint.backend.modules.usersRegistry.PointRegistryEntity;
import com.nectopoint.backend.repositories.PointRegistryRepository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;



@RestController
@RequestMapping("/pontos")
public class PointRegistryController {
    
    @Autowired
    private PointRegistryRepository registryRepo;

    @PostMapping("/bater-ponto")
    public PointRegistryEntity create(@RequestBody PointRegistryDTO requestData) {
        PointRegistryEntity record = new PointRegistryEntity();

        record.setId_colaborador(requestData.getId_colaborador());
        record.setTipo_ponto(requestData.getTipo_ponto());
        record.setData_hora(Instant.now());

        return registryRepo.save(record);
    }

    @GetMapping("/historico")
    public String getMethodName(@RequestParam String param) {
        return new String();
    }
    
}
