package com.nectopoint.backend.repositories.userSession;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.nectopoint.backend.modules.user.UserSessionEntity;

public interface UserSessionRepositoryCustom {
    Page<UserSessionEntity> findByParamsDynamic(String cpf, Pageable pageable);
}
