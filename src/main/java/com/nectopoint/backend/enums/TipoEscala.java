package com.nectopoint.backend.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum TipoEscala {
    CINCO_X_DOIS,
    SEIS_X_UM;

    @JsonCreator
    public static TipoEscala fromString(String value) {
        for (TipoEscala tipo : TipoEscala.values()) {
            if (tipo.name().equalsIgnoreCase(value)) {
                return tipo;
            }
        }
        throw new IllegalArgumentException("Valor para tipo_escala inválido: " + value);
    }

    @JsonValue
    public String toValue() {
        return name();
    }
}
