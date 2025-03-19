package com.nectopoint.backend.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum TipoCargo {
    GERENTE,
    COLABORADOR;

    @JsonCreator
    public static TipoCargo fromString(String value) {
        for (TipoCargo tipo : TipoCargo.values()) {
            if (tipo.name().equalsIgnoreCase(value)) {
                return tipo;
            }
        }
        throw new IllegalArgumentException("Valor para tipo_cargo inválido: " + value);
    }

    @JsonValue
    public String toValue() {
        return name();
    }
}
