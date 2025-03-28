package com.nectopoint.backend.services;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nectopoint.backend.dtos.UserDetailsDTO;
import com.nectopoint.backend.enums.TipoAviso;
import com.nectopoint.backend.enums.TipoStatusTurno;
import com.nectopoint.backend.modules.shared.PointRegistryStripped;
import com.nectopoint.backend.modules.user.UserEntity;
import com.nectopoint.backend.modules.user.UserSessionEntity;
import com.nectopoint.backend.modules.usersRegistry.PointRegistryEntity;
import com.nectopoint.backend.modules.usersRegistry.WarningsEntity;
import com.nectopoint.backend.repositories.UserRepository;
import com.nectopoint.backend.repositories.pointRegistry.PointRegistryRepository;
import com.nectopoint.backend.repositories.userSession.UserSessionRepository;
import com.nectopoint.backend.utils.DataTransferHelper;

@Service
public class UserSessionService {

    private final DataTransferHelper dataTransferHelper;
    
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

    public UserSessionService (DataTransferHelper dataTransferHelper) {
        this.dataTransferHelper = dataTransferHelper;
    }

    public void finishShift(PointRegistryEntity targetShift) {
        WarningsEntity warning;
        TipoAviso tipo_aviso;

        Long id_colaborador = targetShift.getId_colaborador();
        String id_registro = targetShift.getId_registro();

        UserSessionEntity targetUser = userSessionRepo.findByColaborador(id_colaborador);
        String nome_colaborador = targetUser.getDados_usuario().getNome();
        String cpf_colaborador = targetUser.getDados_usuario().getCpf();

        UserEntity targetUserSQL = userRepo.findById(id_colaborador).get();

        if ("inativo".equals(id_registro)) {
            targetShift = new PointRegistryEntity();
            targetShift.setId_colaborador(id_colaborador);
            targetShift.setNome_colaborador(nome_colaborador);
            targetShift.setCpf_colaborador(cpf_colaborador);

            // targetShift.setInicio_turno(Instant.now());
            targetShift.setStatus_turno(TipoStatusTurno.NAO_COMPARECEU);
            registryRepo.save(targetShift);

            targetUser.getJornadas_historico().add(dataTransferHelper.toPointRegistryStripped(targetShift));

            targetUserSQL.missedWorkDay();
            targetUser.missedWorkDay();

            userRepo.save(targetUserSQL);
            userSessionRepo.save(targetUser);
        } else if (targetShift.getPontos_marcados().size()%2 != 0) {
            if (id_registro.equals(targetUser.getJornada_atual().getId_registro())) {
                targetUser.setJornada_atual(new PointRegistryStripped());
            }

            targetShift.setStatus_turno(TipoStatusTurno.IRREGULAR);
            tipo_aviso = TipoAviso.PONTOS_IMPAR;
            warning = warningsService.registerWarning(id_colaborador, nome_colaborador, cpf_colaborador, tipo_aviso);
            targetShift.setId_aviso(warning.getId_aviso());

            registryRepo.save(targetShift);

            targetUser.getJornadas_irregulares().add(dataTransferHelper.toPointRegistryStripped(targetShift));
            targetUser.getAlertas_usuario().add(dataTransferHelper.toWarningsStripped(warning));

            userSessionRepo.save(targetUser);
        } else {
            if (id_registro.equals(targetUser.getJornada_atual().getId_registro())) {
                targetUser.setJornada_atual(new PointRegistryStripped());
            } else {
                targetUser.getJornadas_irregulares().removeIf(jornada -> jornada.getId_registro().equals(id_registro));
            }

            Long horas_trabalhadas_turno = targetShift.getTempo_trabalhado_min();
            Long horas_diarias = (long)targetUser.getJornada_trabalho().getHoras_diarias() * 60;
            Instant fim_turno = targetShift.getPontos_marcados().get(targetShift.getPontos_marcados().size()-1).getData_hora();

            targetShift.setStatus_turno(TipoStatusTurno.ENCERRADO);
            targetShift.setFim_turno(fim_turno);

            if (targetShift.getTirou_almoco() == false && Math.abs(horas_trabalhadas_turno - horas_diarias) < 60) {
                targetShift.setStatus_turno(TipoStatusTurno.IRREGULAR);
                tipo_aviso = TipoAviso.SEM_ALMOCO;
                warning = warningsService.registerWarning(id_colaborador, nome_colaborador, cpf_colaborador, tipo_aviso);
                targetShift.setId_aviso(warning.getId_aviso());

                registryRepo.save(targetShift);

                targetUser.getJornadas_irregulares().add(dataTransferHelper.toPointRegistryStripped(targetShift));
                targetUser.getAlertas_usuario().add(dataTransferHelper.toWarningsStripped(warning));

                Long novo_banco_de_horas = targetUser.getJornada_trabalho().getBanco_de_horas() + (horas_trabalhadas_turno - horas_diarias);
                targetUser.getJornada_trabalho().setBanco_de_horas(novo_banco_de_horas);
                targetUserSQL.setBankOfHours(novo_banco_de_horas);

                userSessionRepo.save(targetUser);
            } else {
                registryRepo.save(targetShift);

                targetUser.getJornadas_historico().add(dataTransferHelper.toPointRegistryStripped(targetShift));

                Long novo_banco_de_horas = targetUser.getJornada_trabalho().getBanco_de_horas() + (horas_trabalhadas_turno - horas_diarias);
                targetUser.getJornada_trabalho().setBanco_de_horas(novo_banco_de_horas);
                targetUserSQL.setBankOfHours(novo_banco_de_horas);

                userRepo.save(targetUserSQL);
                userSessionRepo.save(targetUser);
            }
        }
    }

    public void approveVacation(Long id_colaborador, Instant dataInicioFerias, Integer diasFerias) {
        UserSessionEntity targetUser = userSessionRepo.findByColaborador(id_colaborador);
        targetUser.getDados_usuario().setFerias_inicio(dataInicioFerias);
        Instant feriasFinal = dataInicioFerias.plus(Duration.ofDays(diasFerias));
        targetUser.getDados_usuario().setFerias_final(feriasFinal);
        userSessionRepo.save(targetUser);
    }

    public void createSession(UserDetailsDTO userDetails) {
        UserSessionEntity checkSession = userSessionRepo.findByColaborador(userDetails.getId());
        if (checkSession != null) {
            userSessionRepo.delete(checkSession);
            systemServices.clearUserData(userDetails.getId());
        }
        UserSessionEntity userSession = new UserSessionEntity();
        userSession = dataTransferHelper.toUserSessionEntity(userDetails);

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
        
        if (updateTarget != null) {
            userSessionRepo.delete(updateTarget);
            systemServices.clearUserData(newData.getId());
        }

        UserSessionEntity userSession = new UserSessionEntity();
        userSession = dataTransferHelper.toUserSessionEntity(newData);
        userSessionRepo.save(userSession);
        // userSessionRepo.save(updateTarget);
    }

    public void deleteUserData(Long id_colaborador) {
        UserSessionEntity deleteTarget = userSessionRepo.findByColaborador(id_colaborador);

        userSessionRepo.delete(deleteTarget);
    }

    public void syncUsersWithSessions() {
        List<UserEntity> allUsers = userRepo.findAll();
        for (UserEntity user : allUsers) {
            UserDetailsDTO userDetailsDTO = dataTransferHelper.toUserDetailsDTO(user);
            
            createSession(userDetailsDTO);
        }
    }
}
