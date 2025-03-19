package com.nectopoint.backend.services;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nectopoint.backend.dtos.PointRegistryDTO;
import com.nectopoint.backend.enums.TipoPonto;
import com.nectopoint.backend.enums.TipoStatus;
import com.nectopoint.backend.enums.TipoStatusTurno;
import com.nectopoint.backend.modules.user.UserSessionEntity;
import com.nectopoint.backend.modules.usersRegistry.PointRegistryEntity;
import com.nectopoint.backend.modules.usersRegistry.PointRegistryEntity.Ponto;
import com.nectopoint.backend.repositories.PointRegistryRepository;
import com.nectopoint.backend.repositories.UserSessionRepository;

@Service
public class PointRegistryService {
    
    @Autowired
    private PointRegistryRepository registryRepo;
    @Autowired
    private UserSessionRepository userSessionRepo;

    @Autowired
    private UserSessionService userSessionService;
    @Autowired
    private WarningsService warningsService;
    @Autowired
    private TicketsService ticketsService;

    public PointRegistryEntity postPunch(Long id_colaborador) {
        UserSessionEntity currentUser = userSessionRepo.findByColaborador(id_colaborador);
        PointRegistryEntity currentShift;

        String checkShift = currentUser.getJornada_atual().getId_registro();
        Instant data_hora = Instant.now();
        
        if ("inativo".equals(checkShift)) {
            currentShift = new PointRegistryEntity(id_colaborador);
            Ponto ponto_atual = new Ponto();

            currentShift.setInicio_turno(data_hora);
            ponto_atual.setData_hora(data_hora);
            ponto_atual.setTipo_ponto(TipoPonto.ENTRADA);
            currentShift.getPontos_marcados().add(ponto_atual);
            currentShift.setStatus_turno(TipoStatusTurno.TRABALHANDO);
        } else {
            currentShift = processNewEntry(registryRepo.findById(checkShift).get(), data_hora, false);
        }

        registryRepo.save(currentShift);
        currentUser.setJornada_atual(currentShift);

        userSessionRepo.save(currentUser);
        return currentShift;
    }

    public void correctPointPunch(PointRegistryDTO correctionData) {
        String id_registro = correctionData.getId_registro();
        String id_aviso = correctionData.getDados_ticket().getId_aviso();
        String id_ticket = correctionData.getDados_ticket().getId_ticket();

        Instant data_hora = correctionData.getData_hora();
        TipoStatus status_resolvido = TipoStatus.RESOLVIDO;

        PointRegistryEntity targetShift = processNewEntry(registryRepo.findById(id_registro).get(), data_hora, true);

        userSessionService.finishShift(targetShift);

        ticketsService.changeStatus(id_ticket, status_resolvido);
        warningsService.changeStatus(id_aviso, status_resolvido);

        registryRepo.save(targetShift);
    }

    public void endDayShifts() {
        List<UserSessionEntity> userSessions = userSessionRepo.findAll();

        for (UserSessionEntity user : userSessions) {
            userSessionService.finishShift(user.getJornada_atual());
        }
    }

    private PointRegistryEntity processNewEntry(PointRegistryEntity targetShift, Instant date_time, Boolean close_shift) {
        Ponto newPoint = new Ponto();

        int last_index = targetShift.getPontos_marcados().size()-1;
        Ponto last_entry = targetShift.getPontos_marcados().get(last_index);
        
        newPoint.setData_hora(date_time);
        newPoint.setTipo_ponto(last_entry.getTipo_ponto().invert());

        Instant time_last_entry = last_entry.getData_hora();
        Long time_between = Duration.between(time_last_entry, date_time).toMinutes();

        if (newPoint.getTipo_ponto() == TipoPonto.SAIDA) {
            targetShift.setTempo_trabalhado_min(targetShift.getTempo_trabalhado_min()+time_between);
            targetShift.setStatus_turno(!close_shift ? TipoStatusTurno.INTERVALO : TipoStatusTurno.ENCERRADO);
        } else {
            targetShift.setTempo_intervalo_min(targetShift.getTempo_intervalo_min()+time_between);
            targetShift.setStatus_turno(TipoStatusTurno.TRABALHANDO);
        }

        targetShift.getPontos_marcados().add(newPoint);

        return targetShift;
    }

    //Deletar TODOS os registros de pontos de um usuário
    //ESSA FUNÇÃO É APENAS PARA SINCRONIZAR OS BANCOS DURANTE DESENVOLVIMENTO
    public void deleteAllByColaborador (Long id) {
        List<PointRegistryEntity> records = registryRepo.findAllByIdColaborador(id);
        registryRepo.deleteAll(records);
    }

}
