package com.nectopoint.backend.services;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nectopoint.backend.enums.TipoAviso;
import com.nectopoint.backend.enums.TipoPonto;
import com.nectopoint.backend.modules.user.UserSessionEntity;
import com.nectopoint.backend.modules.usersRegistry.PointRegistryEntity;
import com.nectopoint.backend.modules.usersRegistry.WarningsEntity;
import com.nectopoint.backend.repositories.PointRegistryRepository;
import com.nectopoint.backend.repositories.UserRepository;
import com.nectopoint.backend.repositories.UserSessionRepository;

@Service
public class PointRegistryService {
    
    @Autowired
    private PointRegistryRepository registryRepo;
    @Autowired
    private UserRepository userRepo;
    @Autowired
    private UserSessionRepository userSessionRepo;

    @Autowired
    private WarningsService warningsService;

    public void endOfDayProcesses(){
        LocalDate previousDay = LocalDate.now().minusDays(1);
        Instant start = previousDay.atStartOfDay().toInstant(ZoneOffset.UTC);
        Instant end = previousDay.atTime(23, 59, 59).toInstant(ZoneOffset.UTC);
        Instant data_aviso = start;

        List<PointRegistryEntity> pointsFromPreviousDay = registryRepo.findAllByDate(start, end);

        Map<Long, List<PointRegistryEntity>> groupByCollaborator = pointsFromPreviousDay.stream()
        .collect(Collectors.groupingBy(
            PointRegistryEntity::getId_colaborador,
            TreeMap::new,
            Collectors.collectingAndThen(
                Collectors.toList(), list -> {
                    list.sort(Comparator.comparing(PointRegistryEntity::getData_hora).reversed());
                    return list;
                }
            )
        ));

        for (Map.Entry<Long, List<PointRegistryEntity>> collaboratorRecords : groupByCollaborator.entrySet()) {
            Long id_colaborador = collaboratorRecords.getKey();
            List<PointRegistryEntity> pontos_marcados = collaboratorRecords.getValue();
            UserSessionEntity currentUser = userSessionRepo.findByColaborador(id_colaborador);
            
            if(!pontos_marcados.isEmpty() && pontos_marcados.get(0).getTipo_ponto() == TipoPonto.ENTRADA) {
                WarningsEntity warning = warningsService.registerWarning(id_colaborador, TipoAviso.PONTOS_IMPAR, data_aviso, pontos_marcados);

                currentUser.getAlertas_usuario().add(warning);
                currentUser.getJornada_trabalho().getJornada_atual().setBatida_atual(TipoPonto.ENTRADA);
                userSessionRepo.save(currentUser);
            } else {
                calcularBancoDeHoras(currentUser, pontos_marcados);
            }
        }
    }

    private void calcularBancoDeHoras(UserSessionEntity currentUser, List<PointRegistryEntity> pontos_marcados) {
        Float banco_de_horas_atual = currentUser.getJornada_trabalho().getBanco_de_horas();
        Integer horas_diarias = currentUser.getJornada_trabalho().getHoras_diarias();

        if (pontos_marcados.isEmpty()) {
            currentUser.getJornada_trabalho().setBanco_de_horas(banco_de_horas_atual - horas_diarias);
            currentUser.getJornada_trabalho().getJornada_atual().setBatida_atual(TipoPonto.ENTRADA);
            userSessionRepo.save(currentUser);

            userRepo.updateMissedWorkDay(currentUser.getId_colaborador());
        } else {
            Float totalHours = (float)0;

            for (PointRegistryEntity ponto : pontos_marcados) {
                if (ponto.getTipo_ponto() == TipoPonto.SAIDA) {
                    totalHours += ponto.getHoras_trabalhadas();
                }
            }

            Float banco_de_horas = banco_de_horas_atual + (totalHours - horas_diarias);
            currentUser.getJornada_trabalho().setBanco_de_horas(banco_de_horas);
            currentUser.getJornada_trabalho().getJornada_atual().setBatida_atual(TipoPonto.ENTRADA);
            userSessionRepo.save(currentUser);
            
            userRepo.updateBankOfHours(currentUser.getId_colaborador(), banco_de_horas);
        }
    }

}
