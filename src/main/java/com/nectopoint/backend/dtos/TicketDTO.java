package com.nectopoint.backend.dtos;

import com.nectopoint.backend.enums.TipoTicket;
import com.nectopoint.backend.modules.usersRegistry.WarningsEntity;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TicketDTO {
    @NotNull(message = "O ID do colaborador é obrigatório!")
    private Long id_colaborador;

    @NotNull(message = "O tipo do ticket é obrigatório!")
    private TipoTicket tipo_ticket;

    private String mensagem;

    private WarningsEntity aviso_atrelado;
}
