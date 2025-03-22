package com.nectopoint.backend.dtos;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.modelmapper.ModelMapper;

import com.nectopoint.backend.enums.TipoTicket;
import com.nectopoint.backend.modules.usersRegistry.TicketsEntity;
import com.nectopoint.backend.validators.tickets.ValidTicket;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@ValidTicket
public class TicketDTO {
    @NotNull(message = "O tipo do ticket é obrigatório!")
    private TipoTicket tipo_ticket;

    // Usado para resolver tipo PONTOS_IMPAR
    private LocalTime horario_saida;

    // Usado para resolver tipo SEM_ALMOCO
    private LocalTime inicio_intervalo;
    private LocalTime fim_intervalo;

    // Usado para PEDIR_FERIAS
    private LocalDate data_inicio_ferias;
    private Integer dias_ferias;

    // Usado para PEDIR_ABONO, informando dia ou dias de abono,
    // o intervalo de horas (00:00h às 23:59h caso seja um dia inteiro) e o motivo
    private String motivo_abono;
    private List<LocalDate> dias_abono;
    private LocalTime abono_inicio;
    private LocalTime abono_final;

    private String mensagem;

    // Objetos de turno e alertas devem estar atrelados caso ticket seja
    // do tipo PONTOS_IMPAR ou SEM_ALMOCO
    private String id_registro;
    private String id_aviso;

    public TicketsEntity toTicketsEntity() {
        ModelMapper modelMapper = new ModelMapper();
        return modelMapper.map(this, TicketsEntity.class);
    }
}
