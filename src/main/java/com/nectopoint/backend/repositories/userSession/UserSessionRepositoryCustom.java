package com.nectopoint.backend.repositories.userSession;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.nectopoint.backend.dtos.UserSessionDTO;
import com.nectopoint.backend.enums.TipoStatusUsuario;
import com.nectopoint.backend.modules.user.UserSessionEntity;

public interface UserSessionRepositoryCustom {
    Page<UserSessionDTO> findByParamsDynamic(String cpf, String nome_colaborador, List<TipoStatusUsuario> lista_status, Pageable pageable);
    // List<UserSessionDTO> findEmployeesOnLeave();
    List<UserSessionEntity> findEmployeesNotOnLeave();
}
