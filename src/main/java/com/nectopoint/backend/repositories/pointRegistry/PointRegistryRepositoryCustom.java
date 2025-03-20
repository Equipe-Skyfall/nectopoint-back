package com.nectopoint.backend.repositories.pointRegistry;

import java.time.Instant;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.nectopoint.backend.enums.TipoStatusTurno;
import com.nectopoint.backend.modules.usersRegistry.PointRegistryEntity;

public interface PointRegistryRepositoryCustom {
    Page<PointRegistryEntity> findByParamsDynamic(Long id_colaborador, Instant start, Instant end,
                                                    TipoStatusTurno status_turno, Pageable pageable);
}
