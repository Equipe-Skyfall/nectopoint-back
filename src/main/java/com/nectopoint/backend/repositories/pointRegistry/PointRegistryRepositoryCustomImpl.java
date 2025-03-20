package com.nectopoint.backend.repositories.pointRegistry;

import java.time.Instant;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.nectopoint.backend.enums.TipoStatusTurno;
import com.nectopoint.backend.modules.usersRegistry.PointRegistryEntity;

public class PointRegistryRepositoryCustomImpl implements PointRegistryRepositoryCustom {
    
    @Autowired
    private MongoTemplate mongoTemplate;

    public Page<PointRegistryEntity> findByParamsDynamic(Long id_colaborador, Instant start, Instant end,
                                                    TipoStatusTurno status_turno, Pageable pageable
    ) {
        Query query = new Query();

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

        if (status_turno != null) {
            query.addCriteria(Criteria.where("status_aviso").is(status_turno));
        }

        long total = mongoTemplate.count(query, PointRegistryEntity.class);

        query.with(pageable);

        List<PointRegistryEntity> registry = mongoTemplate.find(query, PointRegistryEntity.class);
        return new PageImpl<>(registry, pageable, total);
    }
}
