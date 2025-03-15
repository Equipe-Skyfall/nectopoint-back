package com.nectopoint.backend.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum TipoPonto {
    ENTRADA, SAIDA;

    @JsonCreator
    public static TipoPonto fromString(String value) {
        for (TipoPonto tipo : TipoPonto.values()) {
            if (tipo.name().equalsIgnoreCase(value)) {
                return tipo;
            }
        }
        throw new IllegalArgumentException("Valor para tipo_ponto inválido: " + value);
    }

    @JsonValue
    public String toValue() {
        return name();
    }
}
