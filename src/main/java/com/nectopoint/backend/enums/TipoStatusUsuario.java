package com.nectopoint.backend.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum TipoStatusUsuario {
    ESCALADO("Trabalhando"),
    FORA_DO_EXPEDIENTE("Fora do expediente"),
    FOLGA("De Folga"),
    FERIAS("Em Férias"),
    INATIVO("Inativo"),//inativo para não deletar o usuario
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