package com.nectopoint.backend.repositories;

import java.time.Instant;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.nectopoint.backend.enums.TipoStatusTurno;
import com.nectopoint.backend.modules.usersRegistry.PointRegistryEntity;

public interface PointRegistryRepository extends MongoRepository<PointRegistryEntity, String> {
    
    @Query("{ 'id_colaborador' : ?0 }")
    List<PointRegistryEntity> findAllByIdColaborador(Long id_colaborador);

    @Query("{ $and: [ { 'id_colaborador': { $eq: ?0 } }, { 'inicio_turno': { $gte: ?1, $lte: ?2 } }, { 'status_turno': { $eq: ?3 } } } ] }")
    Page<PointRegistryEntity> findByParams(Long id_colaborador, Instant start, Instant end, TipoStatusTurno status_turno, Pageable pageable);

}
