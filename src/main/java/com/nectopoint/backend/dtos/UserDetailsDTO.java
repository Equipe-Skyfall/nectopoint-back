package com.nectopoint.backend.dtos;

import com.nectopoint.backend.enums.TipoCargo;
import com.nectopoint.backend.enums.TipoEscala;
import com.nectopoint.backend.enums.TipoStatusUsuario;

import lombok.Data;

@Data
public class UserDetailsDTO {
    private Long id;
    private String name;
    private String cpf;
    private TipoCargo title;
    private String department;
    private TipoEscala workJourneyType;
    private Long bankOfHours;
    private Integer dailyHours;
    private TipoStatusUsuario status;
}
