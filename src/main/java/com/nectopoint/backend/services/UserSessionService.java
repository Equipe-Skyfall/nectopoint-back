package com.nectopoint.backend.services;

import java.time.Instant;
import java.util.List;
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
import com.nectopoint.backend.repositories.pointRegistry.PointRegistryRepository;
import com.nectopoint.backend.repositories.userSession.UserSessionRepository;

@Service
public class UserSessionService {
    
    @Autowired
    private UserSessionRepository userSessionRepo;
    @Autowired
    private UserRepository userRepo;
    @Autowired
    private PointRegistryRepository registryRepo;

    @Autowired
    private SystemServices systemServices;
    @Autowired
    private WarningsService warningsService;


    public void finishShift(PointRegistryEntity targetShift) {
        Boolean register_warning = false;
        String mensagem="";
        TipoAviso tipo_aviso=TipoAviso.PONTOS_IMPAR;

        Long id_colaborador = targetShift.getId_colaborador();
        String id_registro = targetShift.getId_registro();
        UserSessionEntity targetUser = userSessionRepo.findByColaborador(id_colaborador);
        UserEntity targetUserSQL = userRepo.findById(id_colaborador).get();

        if ("inativo".equals(id_registro)) {
            targetShift = new PointRegistryEntity(id_colaborador, targetUser.getDados_usuario().getNome());
            targetShift.setInicio_turno(Instant.now());
            targetShift.setStatus_turno(TipoStatusTurno.NAO_COMPARECEU);

            targetUser.getJornadas_historico().add(targetShift);

            targetUserSQL.missedWorkDay();
            targetUser.missedWorkDay();
        } else if (targetShift.getPontos_marcados().size()%2 != 0) {
            if (id_registro.equals(targetUser.getJornada_atual().getId_registro())) {
                targetUser.setJornada_atual(new PointRegistryEntity(id_colaborador, targetUser.getDados_usuario().getNome()));
                targetUser.getJornada_atual().setId_registro("inativo");
            }
            targetShift.setStatus_turno(TipoStatusTurno.IRREGULAR);
            targetUser.getJornadas_irregulares().add(targetShift);

            register_warning = true;
            mensagem = "Pontos ímpares registrados!";
            tipo_aviso = TipoAviso.PONTOS_IMPAR;
        } else {

            if (id_registro.equals(targetUser.getJornada_atual().getId_registro())) {
                targetUser.setJornada_atual(new PointRegistryEntity(id_colaborador, targetUser.getDados_usuario().getNome()));
                targetUser.getJornada_atual().setId_registro("inativo");
            } else {
                targetUser.getJornadas_irregulares().removeIf(jornada -> jornada.getId_registro().equals(id_registro));
            }

            Long intervalo_turno = targetShift.getTempo_intervalo_min();
            Long horas_trabalhadas_turno = targetShift.getTempo_trabalhado_min();
            Long horas_diarias = (long)targetUser.getJornada_trabalho().getHoras_diarias() * 60;

            targetShift.setStatus_turno(TipoStatusTurno.ENCERRADO);

            if (intervalo_turno < 60 && Math.abs(horas_trabalhadas_turno - horas_diarias) < 60) {
                targetShift.setStatus_turno(TipoStatusTurno.IRREGULAR);
                targetUser.getJornadas_irregulares().add(targetShift);

                register_warning = true;
                mensagem = "Turno finalizado sem almoço!";
                tipo_aviso = TipoAviso.SEM_ALMOCO;
            } else {
                targetUser.getJornadas_historico().add(targetShift);
            }

            Long novo_banco_de_horas = targetUser.getJornada_trabalho().getBanco_de_horas() + (horas_trabalhadas_turno - horas_diarias);

            targetUser.getJornada_trabalho().setBanco_de_horas(novo_banco_de_horas);
            targetUserSQL.setBankOfHours(novo_banco_de_horas);
        }
        
        registryRepo.save(targetShift);
        userRepo.save(targetUserSQL);
        userSessionRepo.save(targetUser);
        if (register_warning) {
            warningsService.registerWarning(id_colaborador,
                                            tipo_aviso,
                                            Optional.ofNullable(mensagem),
                                            Optional.ofNullable(targetShift));
        }
    }

    public void createSession(UserDetailsDTO userDetails) {
        UserSessionEntity checkSession = userSessionRepo.findByColaborador(userDetails.getId());
        if (checkSession != null) {
            userSessionRepo.delete(checkSession);
            systemServices.clearUserData(userDetails.getId());
        }
        UserSessionEntity userSession = UserSessionEntity.fromUserDetailsDTO(userDetails);

        userSessionRepo.save(userSession);
    }

    public void startSession(UserDetailsDTO userDetails) {
        UserSessionEntity userSession = userSessionRepo.findByColaborador(userDetails.getId());

        userSession.getDados_usuario().setNome(userDetails.getName());
        userSession.getDados_usuario().setCpf(userDetails.getCpf());
        userSession.getDados_usuario().setCargo(userDetails.getTitle());
        userSession.getDados_usuario().setDepartamento(userDetails.getDepartment());

        userSession.getJornada_trabalho().setBanco_de_horas(userDetails.getBankOfHours());
        userSession.getJornada_trabalho().setHoras_diarias(userDetails.getDailyHours());
        userSession.getJornada_trabalho().setTipo_jornada(userDetails.getWorkJourneyType());

        userSessionRepo.save(userSession);
    }

    public void updateUser(UserDetailsDTO newData) {
        UserSessionEntity updateTarget = userSessionRepo.findByColaborador(newData.getId());
        
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

    public void syncUsersWithSessions() {
        List<UserEntity> allUsers = userRepo.findAll();
        for (UserEntity user : allUsers) {
            UserDetailsDTO userDetailsDTO =
                                new UserDetailsDTO(
                                    user.getId(),
                                    user.getName(),
                                    user.getCpf(),
                                    user.getTitle(),
                                    user.getDepartment(),
                                    user.getWorkJourneyType(),
                                    user.getBankOfHours(),
                                    user.getDailyHours()
                                );
            
            createSession(userDetailsDTO);
        }
    }
}
