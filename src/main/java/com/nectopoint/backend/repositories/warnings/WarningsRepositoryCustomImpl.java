package com.nectopoint.backend.repositories.warnings;

import java.time.Instant;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.nectopoint.backend.enums.TipoAviso;
import com.nectopoint.backend.enums.TipoStatus;
import com.nectopoint.backend.modules.usersRegistry.WarningsEntity;

public class WarningsRepositoryCustomImpl implements WarningsRepositoryCustom {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public Page<WarningsEntity> findByParamsDynamic(Long id_colaborador, Instant start, Instant end,
                                                      TipoStatus status_aviso, TipoAviso tipo_aviso,
                                                      Pageable pageable) {
        Query query = new Query();

        // Build query dynamically based on non-null parameters
        if (id_colaborador != null) {
            query.addCriteria(Criteria.where("id_colaborador").is(id_colaborador));
        }

        // For the date field 'data_aviso'
        if (start != null && end != null) {
            query.addCriteria(Criteria.where("data_aviso").gte(start).lte(end));
        } else if (start != null) {
            query.addCriteria(Criteria.where("data_aviso").gte(start));
        } else if (end != null) {
            query.addCriteria(Criteria.where("data_aviso").lte(end));
        }

        if (status_aviso != null) {
            query.addCriteria(Criteria.where("status_aviso").is(status_aviso));
        }

        if (tipo_aviso != null) {
            query.addCriteria(Criteria.where("tipo_aviso").is(tipo_aviso));
        }

        // Count total results for pagination
        long total = mongoTemplate.count(query, WarningsEntity.class);

        // Apply pagination
        query.with(pageable);

        List<WarningsEntity> warnings = mongoTemplate.find(query, WarningsEntity.class);
        return new PageImpl<>(warnings, pageable, total);
    }
}
