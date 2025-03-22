package com.nectopoint.backend.dtos;

import com.nectopoint.backend.enums.TipoStatusTicket;
import com.nectopoint.backend.modules.usersRegistry.TicketsEntity;
import com.nectopoint.backend.validators.tickets.ValidTicketAnswer;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@ValidTicketAnswer
public class TicketAnswerDTO {
    @NotNull(message = "Um novo status é obrigatório!")
    private TipoStatusTicket novo_status;
    private String justificativa;
    @NotNull(message = "O objeto do ticket é obrigatório!")
    private TicketsEntity ticket;
}
