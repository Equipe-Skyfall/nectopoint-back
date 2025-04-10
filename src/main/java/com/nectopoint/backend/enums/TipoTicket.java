package com.nectopoint.backend.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum TipoTicket {
    ALTERAR_PONTOS,
    PEDIR_FERIAS,
    PEDIR_ABONO;

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