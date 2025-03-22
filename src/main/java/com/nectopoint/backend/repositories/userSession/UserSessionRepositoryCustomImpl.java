package com.nectopoint.backend.repositories.userSession;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.nectopoint.backend.modules.user.UserSessionEntity;

public class UserSessionRepositoryCustomImpl implements UserSessionRepositoryCustom {
    
    @Autowired
    private MongoTemplate mongoTemplate;

    public Page<UserSessionEntity> findByParamsDynamic(String cpf, Pageable pageable) {
        Query query = new Query();

        if (cpf != null) {
            query.addCriteria(Criteria.where("dados_usuario.cpf").is(cpf));
        }
        query.with(Sort.by(Sort.Order.asc("dados_usuario.nome")));

        long total = mongoTemplate.count(query, UserSessionEntity.class);

        query.with(pageable);

        List<UserSessionEntity> users = mongoTemplate.find(query, UserSessionEntity.class);
        return new PageImpl<>(users, pageable, total);
    }
}
