package com.nectopoint.backend.services;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.nectopoint.backend.dtos.PointRegistryDTO.DadosTicket;
import com.nectopoint.backend.enums.TipoAviso;
import com.nectopoint.backend.enums.TipoPonto;
import com.nectopoint.backend.enums.TipoStatus;
import com.nectopoint.backend.modules.shared.WarningsSummary;
import com.nectopoint.backend.modules.user.UserEntity;
import com.nectopoint.backend.modules.user.UserSessionEntity;
import com.nectopoint.backend.modules.usersRegistry.PointRegistryEntity;
import com.nectopoint.backend.modules.usersRegistry.TicketsEntity;
import com.nectopoint.backend.modules.usersRegistry.WarningsEntity;
import com.nectopoint.backend.repositories.PointRegistryRepository;
import com.nectopoint.backend.repositories.TicketsRepository;
import com.nectopoint.backend.repositories.UserRepository;
import com.nectopoint.backend.repositories.UserSessionRepository;
import com.nectopoint.backend.repositories.WarningsRepository;

@Service
public class PointRegistryService {
    
    @Autowired
    private PointRegistryRepository registryRepo;
    @Autowired
    private UserRepository userRepo;
    @Autowired
    private UserSessionRepository userSessionRepo;
    @Autowired
    private WarningsRepository warningsRepo;
    private TicketsRepository ticketsRepo;

    @Autowired
    private WarningsService warningsService;

    public void correctPointPunch(Long id_colaborador, DadosTicket dados_ticket, PointRegistryEntity pointCorrection) {
        String id_aviso = dados_ticket.getId_aviso();
        WarningsEntity warning = warningsRepo.findById(id_aviso).orElse(null);
        TicketsEntity ticket = ticketsRepo.findById(dados_ticket.getId_ticket()).orElse(null);
        UserSessionEntity currentUser = userSessionRepo.findByColaborador(id_colaborador);
        List<PointRegistryEntity> pontos_marcados = warning.getPontos_marcados();
        
        Instant ultima_entrada = pontos_marcados.get(pontos_marcados.size()-1).getData_hora();
        Instant ultima_saida = pointCorrection.getData_hora();
        Long horas_trabalhadas = Duration.between(ultima_entrada, ultima_saida).toMinutes();
        pointCorrection.setHoras_trabalhadas(horas_trabalhadas);

        pontos_marcados.add(pointCorrection);
        warning.setPontos_marcados(pontos_marcados);
        warning.setStatus_aviso(TipoStatus.RESOLVIDO);
        ticket.setStatus_ticket(TipoStatus.RESOLVIDO);

        ticketsRepo.save(ticket);
        warningsRepo.save(warning);
        registryRepo.save(pointCorrection);

        calcularBancoDeHoras(currentUser, pontos_marcados);
    }

    @Scheduled(cron = "0 0 0 * * ?")
    public void endOfDayProcesses(){
        LocalDate previousDay = LocalDate.now().minusDays(1);
        Instant start = previousDay.atStartOfDay().toInstant(ZoneOffset.UTC);
        Instant end = previousDay.atTime(23, 59, 59).toInstant(ZoneOffset.UTC);

        List<PointRegistryEntity> pointsFromPreviousDay = registryRepo.findAllByDateNoPage(start, end);

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
                WarningsEntity warning = warningsService.registerWarning(id_colaborador, TipoAviso.PONTOS_IMPAR, pontos_marcados);
                WarningsSummary warningSummary = new WarningsSummary();

                warningSummary.setId_aviso(warning.getId_aviso());
                warningSummary.setData_aviso(warning.getData_aviso());
                warningSummary.setStatus_aviso(warning.getStatus_aviso());
                warningSummary.setTipo_aviso(warning.getTipo_aviso());

                currentUser.getAlertas_usuario().add(warningSummary);
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
        UserEntity userRelational = userRepo.findById(currentUser.getId_colaborador()).get();

        if (pontos_marcados.isEmpty()) {
            currentUser.getJornada_trabalho().setBanco_de_horas(banco_de_horas_atual - horas_diarias);
            currentUser.getJornada_trabalho().getJornada_atual().setBatida_atual(TipoPonto.ENTRADA);
            userSessionRepo.save(currentUser);

            userRelational.missedWorkDay();
            userRepo.save(userRelational);
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
            
            userRelational.setBankOfHours(banco_de_horas);
            userRepo.save(userRelational);
        }
    }

}
