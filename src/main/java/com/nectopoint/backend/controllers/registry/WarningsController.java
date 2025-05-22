package com.nectopoint.backend.controllers.registry;

import java.time.Instant;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nectopoint.backend.enums.TipoAviso;
import com.nectopoint.backend.enums.TipoStatusAlerta;
import com.nectopoint.backend.modules.usersRegistry.WarningsEntity;
import com.nectopoint.backend.repositories.warnings.WarningsRepository;

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

    @GetMapping("/listar")
    public ResponseEntity<Page<WarningsEntity>> getAllWarnings(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "5") int size,
        @RequestParam(required = false) Instant startDate,
        @RequestParam(required = false) Instant endDate,
        @RequestParam(required = false) List<TipoStatusAlerta> lista_status,
        @RequestParam(required = false) TipoAviso tipo_aviso,
        @RequestParam(required = false) Long id_colaborador
    ) {
        Pageable pageable = PageRequest.of(page, size);

        Page<WarningsEntity> warningPage = warningsRepo.findByParamsDynamic(id_colaborador, startDate, endDate, lista_status, tipo_aviso, pageable);

        return new ResponseEntity<>(warningPage, HttpStatus.OK);
    }
    
}