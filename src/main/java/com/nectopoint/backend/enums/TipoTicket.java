package com.nectopoint.backend.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum TipoTicket {
    PONTOS_IMPAR;

    @JsonCreator
    public static TipoTicket fromString(String value) {
        for (TipoTicket tipo : TipoTicket.values()) {
            if (tipo.name().equalsIgnoreCase(value)) {
                return tipo;
            }
        }
        throw new IllegalArgumentException("Valor para tipo_ticket inválido: " + value);
    }

    @JsonValue
    public String toValue() {
        return name();
    }
}

// Esse Enum possui somente um valor pois está
// previsto que haverá outros tipos de Tickets no futuro.