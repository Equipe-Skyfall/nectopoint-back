package com.nectopoint.backend.repositories.tickets;

import java.time.Instant;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.nectopoint.backend.enums.TipoStatus;
import com.nectopoint.backend.enums.TipoTicket;
import com.nectopoint.backend.modules.usersRegistry.TicketsEntity;

public class TicketsRepositoryCustomImpl implements TicketsRepositoryCustom {
    
    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public Page<TicketsEntity> findByParamsDynamic(Long id_colaborador, Instant start, Instant end,
                                                    TipoStatus status_ticket, TipoTicket tipo_ticket,
                                                    Pageable pageable
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

        if (status_ticket != null) {
            query.addCriteria(Criteria.where("status_aviso").is(status_ticket));
        }

        if (tipo_ticket != null) {
            query.addCriteria(Criteria.where("tipo_aviso").is(tipo_ticket));
        }

        long total = mongoTemplate.count(query, TicketsEntity.class);

        query.with(pageable);

        List<TicketsEntity> tickets = mongoTemplate.find(query, TicketsEntity.class);
        return new PageImpl<>(tickets, pageable, total);
    }
}
