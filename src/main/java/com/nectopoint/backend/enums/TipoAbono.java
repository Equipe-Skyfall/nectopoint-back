package com.nectopoint.backend.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum TipoAbono {
    ATESTADO_MEDICO;

    @JsonCreator
    public static TipoAbono fromString(String value) {
        for (TipoAbono tipo : TipoAbono.values()) {
            if (tipo.name().equalsIgnoreCase(value)) {
                return tipo;
            }
        }
        throw new IllegalArgumentException("Valor para tipo_aviso inválido: " + value);
    }

    @JsonValue
    public String toValue() {
        return name();
    }
}