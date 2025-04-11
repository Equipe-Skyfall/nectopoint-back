package com.nectopoint.backend.services;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import com.nectopoint.backend.enums.TipoAbono;
import com.nectopoint.backend.enums.TipoPonto;
import com.nectopoint.backend.enums.TipoStatusTurno;
import com.nectopoint.backend.enums.TipoStatusUsuario;
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

        return processPostPunch(currentUser);
    }

    public PointRegistryEntity processPostPunch(UserSessionEntity currentUser) {
        PointRegistryEntity currentShift;

        Instant data_hora = Instant.now();
        
        if (currentUser.getJornada_atual().getStatus_turno().equals(TipoStatusTurno.NAO_INICIADO)) {
            currentShift = new PointRegistryEntity();
            currentShift.setId_colaborador(currentUser.getId_colaborador());
            currentShift.setNome_colaborador(currentUser.getDados_usuario().getNome());
            currentShift.setCpf_colaborador(currentUser.getDados_usuario().getCpf());

            currentShift.setInicio_turno(data_hora);
            currentShift.getPontos_marcados().add(processNewEntry(null, data_hora));
            currentShift.setStatus_turno(TipoStatusTurno.TRABALHANDO);
        } else {
            currentShift = dataTransferHelper.toPointRegistryEntity(
                currentUser.getId_colaborador(),
                currentUser.getDados_usuario().getNome(),
                currentUser.getDados_usuario().getCpf(),
                currentUser.getJornada_atual()
            );
            
            int last_index = currentShift.getPontos_marcados().size()-1;
            Ponto last_entry = currentShift.getPontos_marcados().get(last_index);
            Ponto new_entry = processNewEntry(last_entry, data_hora);

            currentShift.getPontos_marcados().add(new_entry);
            currentShift = updateEntity(currentShift, last_index, new_entry);
        }

        registryRepo.save(currentShift);

        currentUser.setJornada_atual(dataTransferHelper.toPointRegistryStripped(currentShift));
        userSessionRepo.save(currentUser);

        return currentShift;
    }

    public void editShift(String id_registro, List<Instant> lista_horas) {
        PointRegistryEntity targetShift = registryRepo.findById(id_registro).get();
        
        targetShift.getPontos_marcados().clear();
        targetShift.setTempo_trabalhado_min((long)0);
        targetShift.setTirou_almoco(false);
        targetShift.setInicio_turno(lista_horas.get(0));

        for (int i = 0; i < lista_horas.size() - 1; i++ ) {
            if (i==0) {
                targetShift.getPontos_marcados().add(processNewEntry(null, lista_horas.get(i)));
            } else {
                targetShift.getPontos_marcados().add(
                    processNewEntry(targetShift.getPontos_marcados().get(i-1), lista_horas.get(i))
                );
                targetShift = updateEntity(targetShift, i-1, targetShift.getPontos_marcados().get(i));
            }
        }

        userSessionService.finishShift(targetShift);
    }

    public void processExcusedAbsence(Long id_colaborador, TipoAbono motivo_abono, List<Instant> dias_abono) {
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

        registryList.forEach(registry -> {
            registry.setAbono(new Abono());
            registry.getAbono().setMotivo_abono(motivo_abono);
            registry.getAbono().setHorarios_abono("Abonado pelo dia.");

            targetUser.getJornada_trabalho().setBanco_de_horas(banco_atual + horas_diarias);
            targetUserSql.setBankOfHours(banco_atual + horas_diarias);
            targetUser.updateRegistry(dataTransferHelper.toPointRegistryStripped(registry));
        });

        registryRepo.saveAll(registryList);
        userSessionRepo.save(targetUser);
        userRepo.save(targetUserSql);
    }

    public void startDayShifts() {
        List<UserSessionEntity> userSessions = userSessionRepo.findEmployeesNotOnLeave(TipoStatusUsuario.FOLGA);

        for (UserSessionEntity user : userSessions) {
            user.getDados_usuario().setStatus(TipoStatusUsuario.ESCALADO);
            userSessionRepo.save(user);
        }
    }

    public void endDayShifts() {
        //Termina o turno para os funcionário que não estão de FOLGA ou Férias
        List<UserSessionEntity> userSessions = userSessionRepo.findEmployeesNotOnLeave(TipoStatusUsuario.FOLGA);

        for (UserSessionEntity user : userSessions) {
            Long id_colaborador = user.getId_colaborador();
            String nome_colaborador = user.getDados_usuario().getNome();
            String cpf_colaborador = user.getDados_usuario().getCpf();

            PointRegistryEntity entity = dataTransferHelper.toPointRegistryEntity(id_colaborador, nome_colaborador, cpf_colaborador, user.getJornada_atual());
            userSessionService.finishShift(entity);
            user.getDados_usuario().setStatus(TipoStatusUsuario.FORA_DO_EXPEDIENTE);
            userSessionRepo.save(user);
        }
    }

    // Termina apenas o turno de um usuario
    public void endDayShift(Long userId){
        UserSessionEntity user = userSessionRepo.findByColaborador(userId);
        String nome_colaborador = user.getDados_usuario().getNome();
        String cpf_colaborador = user.getDados_usuario().getCpf();
        PointRegistryEntity entity;

        if(!user.getJornada_atual().getStatus_turno().equals(TipoStatusTurno.NAO_INICIADO)){
            if(user.getJornada_atual().getStatus_turno().equals(TipoStatusTurno.TRABALHANDO)){
                entity = processPostPunch(user);
                userSessionService.finishShift(entity);
                user.getDados_usuario().setStatus(TipoStatusUsuario.FORA_DO_EXPEDIENTE);
                userSessionRepo.save(user);
            }else{
                entity = dataTransferHelper.toPointRegistryEntity(userId, nome_colaborador, cpf_colaborador, user.getJornada_atual());
                userSessionService.finishShift(entity);
                user.getDados_usuario().setStatus(TipoStatusUsuario.FORA_DO_EXPEDIENTE);
                userSessionRepo.save(user);
            }
        }
    }

    public Ponto processNewEntry(Ponto last_entry, Instant date_time) {
        Ponto newPoint = new Ponto();

        newPoint.setData_hora(date_time);
        if (last_entry == null) {
            newPoint.setTipo_ponto(TipoPonto.ENTRADA);
        } else {
            newPoint.setTipo_ponto(last_entry.getTipo_ponto().invert());

            Instant time_last_entry = last_entry.getData_hora();
            Long time_between = Duration.between(time_last_entry, date_time).toMinutes();
            newPoint.setTempo_entre_pontos(time_between);
        }

        return newPoint;
    }

    private PointRegistryEntity updateEntity(PointRegistryEntity targetEntity, int last_index, Ponto new_entry) {
        if (new_entry.getTipo_ponto() == TipoPonto.SAIDA) {
            targetEntity.setTempo_trabalhado_min(targetEntity.getTempo_trabalhado_min()+new_entry.getTempo_entre_pontos());
            targetEntity.setStatus_turno(TipoStatusTurno.INTERVALO);
        } else {
            targetEntity.setStatus_turno(TipoStatusTurno.TRABALHANDO);
            if (targetEntity.getTirou_almoco() == false && new_entry.getTempo_entre_pontos() >= 60) {
                targetEntity.setTirou_almoco(true);
                targetEntity.getPontos_marcados().get(last_index).setAlmoco(true);
            }
        }

        return targetEntity;
    }

    //Deletar TODOS os registros de pontos de um usuário
    //ESSA FUNÇÃO É APENAS PARA SINCRONIZAR OS BANCOS DURANTE DESENVOLVIMENTO
    public void deleteAllByColaborador (Long id) {
        List<PointRegistryEntity> records = registryRepo.findAllByIdColaborador(id);
        registryRepo.deleteAll(records);
    }

}
