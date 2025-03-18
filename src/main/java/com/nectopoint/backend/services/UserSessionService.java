package com.nectopoint.backend.services;

import java.time.Instant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nectopoint.backend.dtos.UserDetailsDTO;
import com.nectopoint.backend.enums.TipoPonto;
import com.nectopoint.backend.modules.user.UserSessionEntity;
import com.nectopoint.backend.repositories.UserRepository;
import com.nectopoint.backend.repositories.UserSessionRepository;

@Service
public class UserSessionService {
    
    @Autowired
    private UserSessionRepository userSessionRepo;
    private UserRepository userRepo;

    private SystemServices systemServices;

    public void createSession(Long id, UserDetailsDTO userDetails) {
        UserSessionEntity checkSession = userSessionRepo.findByColaborador(id);
        if (checkSession != null) {
            userSessionRepo.delete(checkSession);
            systemServices.clearUserData(id);
        }
        UserSessionEntity userSession = new UserSessionEntity();
        userSession.setId_colaborador(id);

        userSession.getDados_usuario().setNome(userDetails.getNome());
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

        userSession.getDados_usuario().setNome(userDetails.getNome());
        userSession.getDados_usuario().setCpf(userDetails.getCpf());
        userSession.getDados_usuario().setCargo(userDetails.getTitle());
        userSession.getDados_usuario().setDepartamento(userDetails.getDepartment());

        userSession.getJornada_trabalho().setBanco_de_horas(userDetails.getBankOfHours());
        userSession.getJornada_trabalho().setHoras_diarias(userDetails.getDailyHours());
        userSession.getJornada_trabalho().setTipo_jornada(userDetails.getWorkJourneyType());

        userSessionRepo.save(userSession);
    }

    public void updateLastPunch(Long id_colaborador, TipoPonto batida_atual, Instant ultima_entrada) {
        UserSessionEntity usuario = userSessionRepo.findByColaborador(id_colaborador);
        if (usuario != null) {
            if (batida_atual == TipoPonto.ENTRADA) {
                usuario.getJornada_trabalho().getJornada_atual().setUltima_entrada(ultima_entrada);
            }
            usuario.getJornada_trabalho().getJornada_atual().setBatida_atual(batida_atual.invert());

            userSessionRepo.save(usuario);
        }
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
