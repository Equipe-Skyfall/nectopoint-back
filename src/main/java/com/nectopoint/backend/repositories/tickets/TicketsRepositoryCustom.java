package com.nectopoint.backend.repositories.tickets;

import java.time.Instant;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.nectopoint.backend.dtos.TicketEntityDTO;
import com.nectopoint.backend.enums.TipoStatusTicket;
import com.nectopoint.backend.enums.TipoTicket;

public interface TicketsRepositoryCustom {
    Page<TicketEntityDTO> findByParamsDynamic(String nome_colaborador, Instant start, Instant end,
                                               List<TipoStatusTicket> lista_status_ticket, TipoTicket tipo_ticket,
                                               Pageable pageable);
}
