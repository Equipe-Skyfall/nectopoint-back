package com.nectopoint.backend.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum TipoStatusTurno {
    TRABALHANDO,
    INTERVALO,
    ENCERRADO,
    IRREGULAR,
    AGUARDANDO_AJUSTE,
    NAO_COMPARECEU,
    NAO_INICIADO;

    @JsonCreator
    public static TipoStatusTurno fromString(String value) {
        for (TipoStatusTurno tipo : TipoStatusTurno.values()) {
            if (tipo.name().equalsIgnoreCase(value)) {
                return tipo;
            }
        }
        throw new IllegalArgumentException("Valor para tipo_status_turno inválido: " + value);
    }

    @JsonValue
    public String toValue() {
        return name();
    }
}
