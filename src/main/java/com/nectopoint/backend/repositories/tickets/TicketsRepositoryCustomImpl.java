package com.nectopoint.backend.repositories.tickets;

import java.time.Instant;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.nectopoint.backend.enums.TipoStatusTicket;
import com.nectopoint.backend.enums.TipoTicket;
import com.nectopoint.backend.modules.usersRegistry.TicketsEntity;

public class TicketsRepositoryCustomImpl implements TicketsRepositoryCustom {
    
    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public Page<TicketsEntity> findByParamsDynamic(String nome_colaborador, Instant start, Instant end,
                                                    List<TipoStatusTicket> lista_status_ticket, TipoTicket tipo_ticket,
                                                    Pageable pageable
    ) {
        Query query = new Query();

        if (nome_colaborador != null) {
            query.addCriteria(Criteria.where("nome_colaborador").in(nome_colaborador));
        }

        if (start != null && end != null) {
            query.addCriteria(Criteria.where("data_ticket").gte(start).lte(end));
        } else if (start != null) {
            query.addCriteria(Criteria.where("data_ticket").gte(start));
        } else if (end != null) {
            query.addCriteria(Criteria.where("data_ticket").lte(end));
        }

        if (lista_status_ticket != null && !lista_status_ticket.isEmpty()) {
            query.addCriteria(Criteria.where("status_ticket").in(lista_status_ticket));
        }

        if (tipo_ticket != null) {
            query.addCriteria(Criteria.where("tipo_ticket").is(tipo_ticket));
        }
        query.with(Sort.by(Sort.Order.desc("data_ticket")));

        long total = mongoTemplate.count(query, TicketsEntity.class);

        query.with(pageable);

        List<TicketsEntity> tickets = mongoTemplate.find(query, TicketsEntity.class);
        return new PageImpl<>(tickets, pageable, total);
    }
}
