package com.nectopoint.backend.modules.shared;

import java.time.Instant;

import com.nectopoint.backend.enums.TipoStatusTicket;
import com.nectopoint.backend.enums.TipoTicket;

import lombok.Data;

@Data
public class TicketsStripped {
    private String id_ticket;
    private TipoTicket tipo_ticket;
    private Instant data_ticket;

    private TipoStatusTicket status_ticket;
    private String nome_gerente;
    private String justificativa;

    private String mensagem;
}
