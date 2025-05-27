package com.nectopoint.backend.repositories.pointRegistry;

import java.time.Instant;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.query.Criteria;

import com.nectopoint.backend.enums.TipoStatusTurno;
import com.nectopoint.backend.modules.usersRegistry.PointRegistryEntity;

public interface PointRegistryRepositoryCustom {
    Page<PointRegistryEntity> findByParamsDynamic(String nome_colaborador, Instant start, Instant end,
                                                    List<TipoStatusTurno> lista_status_turno, Pageable pageable);

    List<PointRegistryEntity> findByDateCriterias(Long id_colaborador, List<Criteria> criterias);
}
