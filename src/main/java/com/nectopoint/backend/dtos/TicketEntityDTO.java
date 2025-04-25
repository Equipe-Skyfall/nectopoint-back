package com.nectopoint.backend.dtos;

import java.time.Instant;

import com.nectopoint.backend.enums.TipoStatusTicket;
import com.nectopoint.backend.enums.TipoTicket;

import lombok.Data;

@Data
public class TicketEntityDTO {
    private String id_ticket;
    private String nome_colaborador;
    private String cpf_colaborador;
    private TipoTicket tipo_ticket;
    private Instant data_ticket;
    private TipoStatusTicket status_ticket;
}