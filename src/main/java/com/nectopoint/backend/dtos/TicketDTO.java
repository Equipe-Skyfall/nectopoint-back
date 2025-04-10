package com.nectopoint.backend.dtos;

import java.time.Instant;
import java.util.List;

import com.nectopoint.backend.enums.TipoAbono;
import com.nectopoint.backend.enums.TipoTicket;
import com.nectopoint.backend.modules.usersRegistry.PointRegistryEntity.Ponto;
import com.nectopoint.backend.validators.tickets.ValidTicket;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@ValidTicket
public class TicketDTO {
    @NotNull(message = "O tipo do ticket é obrigatório!")
    private TipoTicket tipo_ticket;

    // Usado para resolver tipo ALTERAR_PONTOS
    private List<Ponto> pontos_anterior;
    private List<Ponto> pontos_ajustado;
    private List<Pares> novos_pontos;

    private List<Instant> lista_horas; // Esse campo não é necessário preencher !!!!

    // Usado para PEDIR_FERIAS
    private Instant data_inicio_ferias;
    private Integer dias_ferias;

    // Usado para PEDIR_ABONO, informando dia ou dias de abono,
    // o intervalo de horas (00:00h às 23:59h caso seja um dia inteiro) e o motivo
    private TipoAbono motivo_abono;
    private List<Instant> dias_abono;

    private String mensagem;

    // Id do turno deve estar atrelado caso ticket seja
    // do tipo ALTERAR_PONTOS
    private String id_registro;
    // Id do aviso deve estar atrelado caso turno à ser alterado esteja IRREGULAR
    private String id_aviso;

    @Data
    public static class Pares {
        private Instant horario_saida;
        private Instant horario_entrada;
    }
}
