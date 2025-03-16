package com.nectopoint.backend.repositories;

import java.time.Instant;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.nectopoint.backend.modules.usersRegistry.TicketsEntity;

public interface TicketsRepository extends MongoRepository<TicketsEntity, String> {
    
    Page<TicketsEntity> findAll(Pageable pageable);

    @Query("{ 'data_hora' : { $gte: ?0, $lte: ?1 } }")
    Page<TicketsEntity> findAllByDate(Instant start, Instant end, Pageable pageable);

    @Query("{ 'id_colaborador' : ?0 }")
    Page<TicketsEntity> findByIdColaborador(Long id_colaborador, Pageable pageable);
    
    @Query("{ 'id_colaborador' : ?0, 'data_hora': { $gte: ?1, $lte: ?2 } }")
    Page<TicketsEntity> findByIdColaboradorAndDate(Long id_colaborador, Instant start, Instant end, Pageable pageable);
}
