package com.nectopoint.backend.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nectopoint.backend.dtos.UserDetailsDTO;
import com.nectopoint.backend.enums.TipoAviso;
import com.nectopoint.backend.enums.TipoStatusTurno;
import com.nectopoint.backend.modules.user.UserEntity;
import com.nectopoint.backend.modules.user.UserSessionEntity;
import com.nectopoint.backend.modules.usersRegistry.PointRegistryEntity;
import com.nectopoint.backend.repositories.UserRepository;
import com.nectopoint.backend.repositories.UserSessionRepository;

@Service
public class UserSessionService {
    
    @Autowired
    private UserSessionRepository userSessionRepo;
    @Autowired
    private UserRepository userRepo;

    @Autowired
    private SystemServices systemServices;
    @Autowired
    private WarningsService warningsService;


    public void finishShift(PointRegistryEntity targetShift) {
        Long id_colaborador = targetShift.getId_colaborador();
        Boolean register_warning = false;

        UserSessionEntity targetUser = userSessionRepo.findByColaborador(id_colaborador);
        UserEntity targetUserSQL = userRepo.findById(id_colaborador).get();

        if (targetUser.getJornada_atual().getId_registro() == targetShift.getId_registro()) {
            targetUser.setJornada_atual(new PointRegistryEntity(id_colaborador));
            targetUser.getJornada_atual().setId_registro("inativo");
        } else {
            targetUser.getJornadas_irregulares().removeIf(jornada -> jornada.getId_registro().equals(targetShift.getId_registro()));
        }

        Long intervalo_turno = targetShift.getTempo_intervalo_min();
        Long horas_trabalhadas_turno = targetShift.getTempo_trabalhado_min();

        if (intervalo_turno < 60) {
            targetShift.setStatus_turno(TipoStatusTurno.IRREGULAR);
            targetUser.getJornadas_irregulares().add(targetShift);
            register_warning = true;
        }

        Long horas_diarias = (long)targetUser.getJornada_trabalho().getHoras_diarias() * 60;
        Long novo_banco_de_horas = targetUser.getJornada_trabalho().getBanco_de_horas() + (horas_trabalhadas_turno - horas_diarias);

        targetUser.getJornada_trabalho().setBanco_de_horas(novo_banco_de_horas);
        targetUserSQL.setBankOfHours(novo_banco_de_horas);

        userRepo.save(targetUserSQL);
        userSessionRepo.save(targetUser);
        if (register_warning) {
            String mensagem = "Turno finalizado sem horário de almoço.";
            warningsService.registerWarning(id_colaborador,
                                            TipoAviso.SEM_ALMOCO,
                                            Optional.ofNullable(mensagem),
                                            Optional.ofNullable(targetShift));
        }
    }

    public void createSession(Long id, UserDetailsDTO userDetails) {
        UserSessionEntity checkSession = userSessionRepo.findByColaborador(id);
        if (checkSession != null) {
            userSessionRepo.delete(checkSession);
            systemServices.clearUserData(id);
        }
        UserSessionEntity userSession = new UserSessionEntity(id);

        userSession.getDados_usuario().setNome(userDetails.getName());
        userSession.getDados_usuario().setCpf(userDetails.getCpf());
        userSession.getDados_usuario().setCargo(userDetails.getTitle());
        userSession.getDados_usuario().setDepartamento(userDetails.getDepartment());

        userSession.getJornada_trabalho().setBanco_de_horas(userDetails.getBankOfHours());
        userSession.getJornada_trabalho().setHoras_diarias(userDetails.getDailyHours());
        userSession.getJornada_trabalho().setTipo_jornada(userDetails.getWorkJourneyType());

        userSessionRepo.save(userSession);
    }

    public void startSession(Long id) {
        UserDetailsDTO userDetails = userRepo.findUserDetailsById(id);
        UserSessionEntity userSession = userSessionRepo.findByColaborador(id);

        userSession.getDados_usuario().setNome(userDetails.getName());
        userSession.getDados_usuario().setCpf(userDetails.getCpf());
        userSession.getDados_usuario().setCargo(userDetails.getTitle());
        userSession.getDados_usuario().setDepartamento(userDetails.getDepartment());

        userSession.getJornada_trabalho().setBanco_de_horas(userDetails.getBankOfHours());
        userSession.getJornada_trabalho().setHoras_diarias(userDetails.getDailyHours());
        userSession.getJornada_trabalho().setTipo_jornada(userDetails.getWorkJourneyType());

        userSessionRepo.save(userSession);
    }

    public void updateUser(Long id_colaborador, UserDetailsDTO newData) {
        UserSessionEntity updateTarget = userSessionRepo.findByColaborador(id_colaborador);
        
        updateTarget.getDados_usuario().setCargo(newData.getTitle());
        updateTarget.getDados_usuario().setDepartamento(newData.getDepartment());

        updateTarget.getJornada_trabalho().setBanco_de_horas(newData.getBankOfHours());
        updateTarget.getJornada_trabalho().setHoras_diarias(newData.getDailyHours());
        updateTarget.getJornada_trabalho().setTipo_jornada(newData.getWorkJourneyType());

        userSessionRepo.save(updateTarget);
    }

    public void deleteUserData(Long id_colaborador) {
        UserSessionEntity deleteTarget = userSessionRepo.findByColaborador(id_colaborador);

        userSessionRepo.delete(deleteTarget);
    }
}
