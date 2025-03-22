package com.nectopoint.backend.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum TipoStatusTicket {
    EM_AGUARDO, //EM_AGUARDO sinaliza que está aguardando uma resposta do gerente.
    APROVADO,
    REPROVADO;

    @JsonCreator
    public static TipoStatusTicket fromString(String value) {
        for (TipoStatusTicket tipo : TipoStatusTicket.values()) {
            if (tipo.name().equalsIgnoreCase(value)) {
                return tipo;
            }
        }
        throw new IllegalArgumentException("Valor para tipo_status inválido: " + value);
    }

    @JsonValue
    public String toValue() {
        return name();
    }
}