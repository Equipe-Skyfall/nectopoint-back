package com.nectopoint.backend.repositories.tickets;

import java.time.Instant;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.nectopoint.backend.enums.TipoStatusTicket;
import com.nectopoint.backend.enums.TipoTicket;
import com.nectopoint.backend.modules.usersRegistry.TicketsEntity;

public interface TicketsRepositoryCustom {
    Page<TicketsEntity> findByParamsDynamic(Long id_colaborador, Instant start, Instant end,
                                               TipoStatusTicket status_ticket, TipoTicket tipo_ticket,
                                               Pageable pageable);
}
