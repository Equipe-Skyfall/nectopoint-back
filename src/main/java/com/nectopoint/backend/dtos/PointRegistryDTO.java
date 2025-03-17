package com.nectopoint.backend.dtos;

import java.time.Instant;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PointRegistryDTO {
    @NotNull(message = "O ID do colaborador é obrigatório!")
    private Long id_colaborador;

    @NotNull(message = "A data e horário são obrigatórios!")
    private Instant data_hora;

    @NotNull(message = "Os dados do ticket são obrigatórios!")
    private DadosTicket dados_ticket;

    @Data
    public static class DadosTicket {
        private String id_ticket;
        private String id_aviso;
    }
}
