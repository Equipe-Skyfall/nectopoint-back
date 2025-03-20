package com.nectopoint.backend.dtos;

import java.time.Instant;

import com.nectopoint.backend.modules.usersRegistry.TicketsEntity;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PointRegistryDTO {

    @NotNull(message = "A data e horário são obrigatórios!")
    private Instant data_hora;

    @NotNull(message = "Os dados do ticket são obrigatórios!")
    private TicketsEntity ticket;

}
