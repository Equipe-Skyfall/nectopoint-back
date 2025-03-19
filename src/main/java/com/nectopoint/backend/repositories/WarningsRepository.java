package com.nectopoint.backend.repositories;

import java.time.Instant;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.nectopoint.backend.enums.TipoAviso;
import com.nectopoint.backend.enums.TipoStatus;
import com.nectopoint.backend.modules.usersRegistry.WarningsEntity;

public interface WarningsRepository extends MongoRepository<WarningsEntity, String>{

    @Query("{ 'id_colaborador' : ?0 }")
    List<WarningsEntity> findAllByIdColaborador(Long id_colaborador);

    @Query("{ $and: [ { 'id_colaborador': { $eq: ?0 } }, { 'data_aviso': { $gte: ?1, $lte: ?2 } }, { 'status_aviso': { $eq: ?3 } }, { 'tipo_aviso': { $eq: ?4 } } ] }")
    Page<WarningsEntity> findByParams(Long id_colaborador, Instant start, Instant end, TipoStatus status_aviso, TipoAviso tipo_aviso, Pageable pageable);

}
