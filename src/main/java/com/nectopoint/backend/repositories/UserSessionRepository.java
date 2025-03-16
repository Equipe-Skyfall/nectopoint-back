package com.nectopoint.backend.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.nectopoint.backend.modules.user.UserSessionEntity;

public interface UserSessionRepository extends MongoRepository<UserSessionEntity, String> {
    @Query("{ 'id_colaborador': ?0 }")
    UserSessionEntity findByColaborador(Long id_colaborador);
    
}