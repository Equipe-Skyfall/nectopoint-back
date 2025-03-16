package com.nectopoint.backend.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PointRegistryDTO {
    @NotNull(message = "O ID do colaborador é obrigatório!")
    private Long id_colaborador;
}
