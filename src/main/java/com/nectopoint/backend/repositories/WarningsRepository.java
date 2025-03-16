package com.nectopoint.backend.repositories;

import java.time.Instant;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.nectopoint.backend.modules.usersRegistry.WarningsEntity;

public interface WarningsRepository extends MongoRepository<WarningsEntity, String>{

    Page<WarningsEntity> findAll(Pageable pageable);

    @Query("{ 'data_hora' : { $gte: ?0, $lte: ?1 } }")
    Page<WarningsEntity> findAllByDate(Instant start, Instant end, Pageable pageable);

    @Query("{ 'id_colaborador' : ?0 }")
    Page<WarningsEntity> findByIdColaborador(Long id_colaborador, Pageable pageable);
    
    @Query("{ 'id_colaborador' : ?0, 'data_hora': { $gte: ?1, $lte: ?2 } }")
    Page<WarningsEntity> findByIdColaboradorAndDate(Long id_colaborador, Instant start, Instant end, Pageable pageable);
}
