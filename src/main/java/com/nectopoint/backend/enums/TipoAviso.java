package com.nectopoint.backend.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum TipoAviso {
    PONTOS_IMPAR;

    @JsonCreator
    public static TipoAviso fromString(String value) {
        for (TipoAviso tipo : TipoAviso.values()) {
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

// Esse Enum possui somente um valor pois está
// previsto que haverá outros tipos de Alertas no futuro.