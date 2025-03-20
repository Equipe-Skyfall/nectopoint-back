package com.nectopoint.backend.repositories.tickets;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.nectopoint.backend.modules.usersRegistry.TicketsEntity;

public interface TicketsRepository extends MongoRepository<TicketsEntity, String>, TicketsRepositoryCustom {

    @Query("{ 'id_colaborador' : ?0 }")
    List<TicketsEntity> findAllByIdColaborador(Long id_colaborador);
    
}
