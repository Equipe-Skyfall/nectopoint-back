package com.nectopoint.backend.repositories.userSession;

import java.time.Instant;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.nectopoint.backend.dtos.DashboardDTO;
import com.nectopoint.backend.dtos.UserSessionDTO;
import com.nectopoint.backend.dtos.UserVacationDTO;
import com.nectopoint.backend.enums.TipoEscala;
import com.nectopoint.backend.enums.TipoStatusUsuario;
import com.nectopoint.backend.modules.user.UserSessionEntity;

public interface UserSessionRepositoryCustom {
    Page<UserSessionDTO> findByParamsDynamic(String cpf, String nome_colaborador, List<TipoStatusUsuario> lista_status, Pageable pageable);
    // List<UserSessionDTO> findEmployeesOnLeave();
    List<UserSessionEntity> findEmployeesNotOnLeave(TipoStatusUsuario optionalStatus);

    List<UserSessionEntity> findEmployeesByWorkSchedule(TipoEscala escala);

    List<UserVacationDTO> findEmployeesStartingOrEndingVacation(Instant date);

    DashboardDTO countUserStatuses();
}
