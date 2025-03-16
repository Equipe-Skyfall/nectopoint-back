package com.nectopoint.backend.controllers.registry;

import java.time.Duration;
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
import com.nectopoint.backend.enums.TipoPonto;
import com.nectopoint.backend.modules.user.UserSessionEntity;
import com.nectopoint.backend.modules.usersRegistry.PointRegistryEntity;
import com.nectopoint.backend.repositories.PointRegistryRepository;
import com.nectopoint.backend.repositories.UserSessionRepository;
import com.nectopoint.backend.services.PointRegistryService;
import com.nectopoint.backend.services.UserSessionService;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;



@RestController
@RequestMapping("/pontos")
public class PointRegistryController {
    
    @Autowired
    private PointRegistryRepository registryRepo;
    @Autowired
    private UserSessionRepository userSessionRepo;

    @Autowired
    private UserSessionService userSessionService;
    @Autowired
    private PointRegistryService registryService;

    @PostMapping("/bater-ponto")
    public PointRegistryEntity postPunch(@Valid @RequestBody PointRegistryDTO requestData) {
        UserSessionEntity user = userSessionRepo.findByColaborador(requestData.getId_colaborador());
        PointRegistryEntity record = new PointRegistryEntity();

        Long id_colaborador = requestData.getId_colaborador();
        TipoPonto tipo_ponto = user.getJornada_trabalho().getJornada_atual().getBatida_atual();
        Instant data_hora = record.getData_hora();

        record.setId_colaborador(id_colaborador);
        record.setTipo_ponto(tipo_ponto);
        if (tipo_ponto == TipoPonto.SAIDA) {
            Instant ultima_entrada = user.getJornada_trabalho().getJornada_atual().getUltima_entrada();
            Long horas_trabalhadas = Duration.between(ultima_entrada, data_hora).toMinutes();
            record.setHoras_trabalhadas(horas_trabalhadas);
        }

        userSessionService.updateLastPunch(id_colaborador, tipo_ponto, data_hora);
        return registryRepo.save(record);
    }

    @PostMapping("/bater-ponto/correcao")
    public void postPunchCorrection(@RequestBody PointRegistryDTO requestData) {
        PointRegistryEntity record = new PointRegistryEntity();

        Long id_colaborador = requestData.getId_colaborador();
        TipoPonto tipo_ponto = TipoPonto.SAIDA;
        Instant data_hora = requestData.getData_hora();

        record.setId_colaborador(id_colaborador);
        record.setTipo_ponto(tipo_ponto);
        record.setData_hora(data_hora);

        registryService.correctPointPunch(id_colaborador, requestData.getDados_ticket(), record);
    }
    
    
    @GetMapping("/")
    public PointRegistryEntity getPointById(@RequestParam String id) {
        return registryRepo.findById(id).get();
    }

    @GetMapping("/historico-todos")
    public ResponseEntity<Page<PointRegistryEntity>> historicoTodos(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "5") int size,
        @RequestParam(required = false) Instant startDate,
        @RequestParam(required = false) Instant endDate
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<PointRegistryEntity> pointRegistryPage;
        if (startDate != null && endDate != null) {
            pointRegistryPage = registryRepo.findAllByDate(startDate, endDate, pageable);
        } else {
            pointRegistryPage = registryRepo.findAll(pageable);
        }

        return new ResponseEntity<>(pointRegistryPage, HttpStatus.OK);
    }

    @GetMapping("/historico-usuario")
    public ResponseEntity<Page<PointRegistryEntity>> historicoUsuario(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "5") int size,
        @RequestParam(required = false) Instant startDate,
        @RequestParam(required = false) Instant endDate,
        @RequestParam Long id_colaborador
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<PointRegistryEntity> pointRegistryPage;
        if (startDate != null && endDate != null) {
            pointRegistryPage = registryRepo.findByIdColaboradorAndDate(id_colaborador, startDate, endDate, pageable);
        } else {
            pointRegistryPage = registryRepo.findByIdColaborador(id_colaborador, pageable);
        }

        return new ResponseEntity<>(pointRegistryPage, HttpStatus.OK);
    }
    
}
