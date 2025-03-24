package com.nectopoint.backend.services;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import com.nectopoint.backend.enums.TipoAbono;
import com.nectopoint.backend.enums.TipoPonto;
import com.nectopoint.backend.enums.TipoStatusTurno;
import com.nectopoint.backend.modules.user.UserEntity;
import com.nectopoint.backend.modules.user.UserSessionEntity;
import com.nectopoint.backend.modules.usersRegistry.PointRegistryEntity;
import com.nectopoint.backend.modules.usersRegistry.PointRegistryEntity.Abono;
import com.nectopoint.backend.modules.usersRegistry.PointRegistryEntity.Ponto;
import com.nectopoint.backend.repositories.UserRepository;
import com.nectopoint.backend.repositories.pointRegistry.PointRegistryRepository;
import com.nectopoint.backend.repositories.userSession.UserSessionRepository;
import com.nectopoint.backend.utils.DataTransferHelper;

@Service
public class PointRegistryService {

    private final DataTransferHelper dataTransferHelper;
    
    @Autowired
    private PointRegistryRepository registryRepo;
    @Autowired
    private UserSessionRepository userSessionRepo;
    @Autowired
    private UserRepository userRepo;

    @Autowired
    private UserSessionService userSessionService;

    public PointRegistryService (DataTransferHelper dataTransferHelper) {
        this.dataTransferHelper = dataTransferHelper;
    }

    public PointRegistryEntity postPunch(Long id_colaborador) {
        UserSessionEntity currentUser = userSessionRepo.findByColaborador(id_colaborador);
        PointRegistryEntity currentShift;

        String checkShift = currentUser.getJornada_atual().getId_registro();
        Instant data_hora = Instant.now();
        
        if ("inativo".equals(checkShift)) {
            currentShift = new PointRegistryEntity();
            currentShift.setId_colaborador(id_colaborador);
            currentShift.setNome_colaborador(currentUser.getDados_usuario().getNome());
            currentShift.setCpf_colaborador(currentUser.getDados_usuario().getCpf());
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
        currentUser.setJornada_atual(dataTransferHelper.toPointRegistryStripped(currentShift));

        userSessionRepo.save(currentUser);
        return currentShift;
    }

    public void correctPointPunch(String id_registro, Instant horario_saida) {
        PointRegistryEntity targetShift = registryRepo.findById(id_registro).get();

        Instant dataHoraTurno = targetShift.getInicio_turno();
        Instant data_hora = joinDateTime(dataHoraTurno, horario_saida);

        targetShift = processNewEntry(targetShift, data_hora, true);

        userSessionService.finishShift(targetShift);

        registryRepo.save(targetShift);
    }

    public void addLunchTime(String id_registro, Instant inicio_intervalo, Instant fim_intervalo) {
        PointRegistryEntity targetShift = registryRepo.findById(id_registro).get();
        UserSessionEntity targetUser = userSessionRepo.findByColaborador(targetShift.getId_colaborador());

        Instant dataHoraTurno = targetShift.getInicio_turno();
        Instant saida_instant = joinDateTime(dataHoraTurno, inicio_intervalo);
        Instant entrada_instant = joinDateTime(dataHoraTurno, fim_intervalo);

        Long time_between_entrada = Duration.between(saida_instant, entrada_instant).toMinutes();
        Ponto entrada = new Ponto();
        entrada.setData_hora(entrada_instant);
        entrada.setTempo_entre_pontos(time_between_entrada);
        entrada.setTipo_ponto(TipoPonto.ENTRADA);

        Long time_between_saida = Duration.between(dataHoraTurno, saida_instant).toMinutes();
        Ponto saida = new Ponto();
        saida.setData_hora(saida_instant);
        saida.setTempo_entre_pontos(time_between_saida);
        saida.setTipo_ponto(TipoPonto.SAIDA);
        saida.setAlmoco(true);

        targetShift.setTirou_almoco(true);
        targetShift.setTempo_trabalhado_min(targetShift.getTempo_trabalhado_min() - time_between_entrada);
        targetShift.setStatus_turno(TipoStatusTurno.ENCERRADO);
        targetShift.getPontos_marcados().add(saida);
        targetShift.getPontos_marcados().add(entrada);
        targetShift.sortPontos();
        
        targetUser.getJornadas_irregulares().removeIf(jornada -> jornada.getId_registro().equals(id_registro));
        targetUser.getJornadas_historico().add(dataTransferHelper.toPointRegistryStripped(targetShift));

        Long updateBankOfHours = targetUser.getJornada_trabalho().getBanco_de_horas() - time_between_entrada;
        targetUser.getJornada_trabalho().setBanco_de_horas(updateBankOfHours);

        UserEntity userSQL = userRepo.findById(targetUser.getId_colaborador()).get();
        userSQL.setBankOfHours(updateBankOfHours);

        registryRepo.save(targetShift);
        userSessionRepo.save(targetUser);
        userRepo.save(userSQL);
    }

    public void processExcusedAbsence(Long id_colaborador, TipoAbono motivo_abono, List<Instant> dias_abono, Instant hora_inicio, Instant hora_final) {
        ZoneId zoneId = ZoneId.of("America/Sao_Paulo");
        List<Criteria> criteriaList = new ArrayList<>();

        UserSessionEntity targetUser = userSessionRepo.findByColaborador(id_colaborador);
        Long banco_atual = targetUser.getJornada_trabalho().getBanco_de_horas();
        Long horas_diarias = (long)targetUser.getJornada_trabalho().getHoras_diarias() * 60;

        UserEntity targetUserSql = userRepo.findById(id_colaborador).get();

        for (Instant date : dias_abono) {
            LocalDate localDate = date.atZone(zoneId).toLocalDate();
            Instant startOfDay = localDate.atStartOfDay(zoneId).toInstant();
            Instant endOfDay = localDate.plusDays(1).atStartOfDay(zoneId).toInstant().minusNanos(1);
    
            Criteria dateCriteria = Criteria.where("inicio_turno").gte(startOfDay).lte(endOfDay);
            criteriaList.add(dateCriteria);
        }

        List<PointRegistryEntity> registryList = registryRepo.findByDateCriterias(criteriaList);
        Long duracao_abono = Duration.between(hora_inicio, hora_final).toMinutes();
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

        registryList.forEach(registry -> {
            registry.setAbono(new Abono());
            registry.getAbono().setMotivo_abono(motivo_abono);
            if (duracao_abono >= horas_diarias) {
                registry.getAbono().setHorarios_abono("Abonado pelo dia.");

                targetUser.getJornada_trabalho().setBanco_de_horas(banco_atual + horas_diarias);
                targetUserSql.setBankOfHours(banco_atual + horas_diarias);
            } else {
                LocalTime localTimeInicio = hora_inicio.atZone(zoneId).toLocalTime();
                LocalTime localTimeFinal = hora_final.atZone(zoneId).toLocalTime();
                String horarios_string = String.format("%s-%s", localTimeInicio.format(formatter), localTimeFinal.format(formatter));
                registry.getAbono().setHorarios_abono(horarios_string);

                targetUser.getJornada_trabalho().setBanco_de_horas(banco_atual + duracao_abono);
                targetUserSql.setBankOfHours(banco_atual + duracao_abono);
            }
            targetUser.updateRegistry(dataTransferHelper.toPointRegistryStripped(registry));
        });

        registryRepo.saveAll(registryList);
        userSessionRepo.save(targetUser);
        userRepo.save(targetUserSql);
    }

    public void endDayShifts() {
        List<UserSessionEntity> userSessions = userSessionRepo.findAll();

        for (UserSessionEntity user : userSessions) {
            Long id_colaborador = user.getId_colaborador();
            String nome_colaborador = user.getDados_usuario().getNome();
            PointRegistryEntity entity = dataTransferHelper.toPointRegistryEntity(id_colaborador, nome_colaborador, user.getJornada_atual());
            userSessionService.finishShift(entity);
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
        newPoint.setTempo_entre_pontos(time_between);

        if (newPoint.getTipo_ponto() == TipoPonto.SAIDA) {
            targetShift.setTempo_trabalhado_min(targetShift.getTempo_trabalhado_min()+time_between);
            targetShift.setStatus_turno(!close_shift ? TipoStatusTurno.INTERVALO : TipoStatusTurno.ENCERRADO);
        } else {
            targetShift.setStatus_turno(TipoStatusTurno.TRABALHANDO);
            if (targetShift.getTirou_almoco() == false && newPoint.getTempo_entre_pontos() >= 60) {
                targetShift.setTirou_almoco(true);
                targetShift.getPontos_marcados().get(last_index).setAlmoco(true);
            }
        }

        targetShift.getPontos_marcados().add(newPoint);

        return targetShift;
    }

    private Instant joinDateTime(Instant dia, Instant hora) {
        ZoneId zone = ZoneId.of("America/Sao_Paulo");
    
        // Extract the date part from 'dia' and the time part from 'hora'
        LocalDate datePart = dia.atZone(zone).toLocalDate();
        LocalTime timePart = hora.atZone(zone).toLocalTime();
        
        // Combine them into a LocalDateTime
        LocalDateTime combined = LocalDateTime.of(datePart, timePart);
        
        // Convert back to an Instant
        return combined.atZone(zone).toInstant();
    }

    //Deletar TODOS os registros de pontos de um usuário
    //ESSA FUNÇÃO É APENAS PARA SINCRONIZAR OS BANCOS DURANTE DESENVOLVIMENTO
    public void deleteAllByColaborador (Long id) {
        List<PointRegistryEntity> records = registryRepo.findAllByIdColaborador(id);
        registryRepo.deleteAll(records);
    }

}
