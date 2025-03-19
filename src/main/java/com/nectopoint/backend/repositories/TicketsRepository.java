package com.nectopoint.backend.repositories;

import java.time.Instant;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.nectopoint.backend.enums.TipoStatus;
import com.nectopoint.backend.enums.TipoTicket;
import com.nectopoint.backend.modules.usersRegistry.TicketsEntity;

public interface TicketsRepository extends MongoRepository<TicketsEntity, String> {

    @Query("{ 'id_colaborador' : ?0 }")
    List<TicketsEntity> findAllByIdColaborador(Long id_colaborador);
    
    @Query("{ $and: [ { 'id_colaborador': { $eq: ?0 } }, { 'data_ticket': { $gte: ?1, $lte: ?2 } }, { 'status_ticket': { $eq: ?3 } }, { 'tipo_ticket': { $eq: ?4 } } ] }")
    Page<TicketsEntity> findByParams(Long id_colaborador, Instant start, Instant end, TipoStatus status_ticket, TipoTicket tipo_ticket, Pageable pageable);

}
