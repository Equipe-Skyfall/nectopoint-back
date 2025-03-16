package com.nectopoint.backend.repositories;

import java.time.Instant;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.nectopoint.backend.modules.usersRegistry.PointRegistryEntity;

public interface PointRegistryRepository extends MongoRepository<PointRegistryEntity, String> {

    Page<PointRegistryEntity> findAll(Pageable pageable);

    @Query("{ 'id_colaborador' : ?0 }")
    Page<PointRegistryEntity> findByIdColaborador(Long id_colaborador, Pageable pageable);

    @Query("{ 'data_hora' : { $gte: ?0, $lte: ?1 } }")
    List<PointRegistryEntity> findAllByDate(Instant start, Instant end);
}
