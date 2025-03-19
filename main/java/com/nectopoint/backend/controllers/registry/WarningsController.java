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

import com.nectopoint.backend.modules.usersRegistry.WarningsEntity;
import com.nectopoint.backend.repositories.WarningsRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;



@RestController
@RequestMapping("/alertas")
public class WarningsController {

    @Autowired
    private WarningsRepository warningsRepo;

    @GetMapping("/{id}")
    public WarningsEntity getWarningById(@PathVariable String id) {
        return warningsRepo.findById(id).get();
    }

    @GetMapping("/alertas-todos")
    public ResponseEntity<Page<WarningsEntity>> getAllWarnings(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "5") int size,
        @RequestParam(required = false) Instant startDate,
        @RequestParam(required = false) Instant endDate
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<WarningsEntity> warningPage;
        if (startDate != null && endDate != null) {
            warningPage = warningsRepo.findAllByDate(startDate, endDate, pageable);
        } else {
            warningPage = warningsRepo.findAll(pageable);
        }

        return new ResponseEntity<>(warningPage, HttpStatus.OK);
    }

    @GetMapping("/alertas-usuario")
    public ResponseEntity<Page<WarningsEntity>> getUserWarnings(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "5") int size,
        @RequestParam(required = false) Instant startDate,
        @RequestParam(required = false) Instant endDate,
        @RequestParam Long id_colaborador
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<WarningsEntity> warningPage;
        if (startDate != null && endDate != null) {
            warningPage = warningsRepo.findByIdColaboradorAndDate(id_colaborador, startDate, endDate, pageable);
        } else {
            warningPage = warningsRepo.findByIdColaborador(id_colaborador, pageable);
        }

        return new ResponseEntity<>(warningPage, HttpStatus.OK);
    }
    
}
