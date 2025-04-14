package com.nectopoint.backend.dtos;

import com.nectopoint.backend.enums.TipoCargo;
import com.nectopoint.backend.enums.TipoEscala;
import com.nectopoint.backend.enums.TipoStatusUsuario;

import lombok.Data;

@Data
public class UserSessionDTO {
    private Long id_colaborador;
    private String nome;
    private String cpf;
    private TipoCargo cargo;
    private String departamento;
    private TipoStatusUsuario status;
    private Long banco_de_horas;
    private Integer horas_diarias;
    private TipoEscala tipo_escala;
}
