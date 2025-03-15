package com.nectopoint.backend.dtos;

import com.nectopoint.backend.enums.TipoPonto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PointRegistryDTO {
    @NotNull(message = "O ID do colaborador é obrigatório!")
    private Long id_colaborador;

    @NotNull(message = "O tipo de batida de ponto é obrigatória!")
    private TipoPonto tipo_ponto;
}
