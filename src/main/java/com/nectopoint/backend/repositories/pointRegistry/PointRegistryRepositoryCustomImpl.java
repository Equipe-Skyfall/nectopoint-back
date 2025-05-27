package com.nectopoint.backend.repositories.pointRegistry;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.nectopoint.backend.enums.TipoStatusTurno;
import com.nectopoint.backend.modules.usersRegistry.PointRegistryEntity;

public class PointRegistryRepositoryCustomImpl implements PointRegistryRepositoryCustom {
    
    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public Page<PointRegistryEntity> findByParamsDynamic(String nome_colaborador, Instant start, Instant end,
                                                    List<TipoStatusTurno> lista_status_turno, Pageable pageable
    ) {
        Query query = new Query();

        if (nome_colaborador != null) {
            query.addCriteria(Criteria.where("nome_colaborador").regex(".*" + Pattern.quote(nome_colaborador) + ".*", "i"));
        }

        if (start != null && end != null) {
            query.addCriteria(Criteria.where("inicio_turno").gte(start).lte(end));
        } else if (start != null) {
            query.addCriteria(Criteria.where("inicio_turno").gte(start));
        } else if (end != null) {
            query.addCriteria(Criteria.where("inicio_turno").lte(end));
        }

        if (lista_status_turno != null && !lista_status_turno.isEmpty()) {
            query.addCriteria(Criteria.where("status_turno").in(lista_status_turno));
        }

        query.with(Sort.by(Sort.Order.desc("inicio_turno")));

        long total = mongoTemplate.count(query, PointRegistryEntity.class);

        query.with(pageable);

        List<PointRegistryEntity> registry = mongoTemplate.find(query, PointRegistryEntity.class);
        return new PageImpl<>(registry, pageable, total);
    }

    public List<PointRegistryEntity> findByDateCriterias(Long id_colaborador, List<Criteria> dateCriterias) {
        List<Criteria> combinedOrs = new ArrayList<>();

        for (Criteria dateCriteria : dateCriterias) {
            combinedOrs.add(
                new Criteria().andOperator(
                    Criteria.where("id_colaborador").is(id_colaborador),
                    Criteria.where("status_turno").is(TipoStatusTurno.NAO_COMPARECEU),
                    dateCriteria
                )
            );
        }

        Criteria finalCriteria = new Criteria().orOperator(combinedOrs.toArray(new Criteria[0]));
        Query query = new Query(finalCriteria);

        return mongoTemplate.find(query, PointRegistryEntity.class);
    }
}
