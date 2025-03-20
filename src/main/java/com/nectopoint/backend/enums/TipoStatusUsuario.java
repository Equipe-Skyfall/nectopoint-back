package com.nectopoint.backend.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum TipoStatusUsuario {
    TRABALHANDO("Trabalhando"),
    EM_FERIAS("Em Férias"),
    LICENCA_MEDICA("Licença Médica"),
    LICENCA_MATERNIDADE("Licença Maternidade"),
    LICENCA_PATERNIDADE("Licença Paternidade"),
    LICENCA_NAO_REMUNERADA("Licença Não Remunerada"),
    OUTROS("Outros");

    @JsonCreator
    public static TipoStatusUsuario fromString(String value) {
        for (TipoStatusUsuario tipo : TipoStatusUsuario.values()) {
            if (tipo.name().equalsIgnoreCase(value)) {
                return tipo;
            }
        }
        throw new IllegalArgumentException("Valor para tipo_status_usuario inválido: " + value);
    }
    
    @JsonValue
    public String toValue() {
        return name();
    }

    private final String descricao;

    TipoStatusUsuario(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}