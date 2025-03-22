package com.nectopoint.backend.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum TipoStatusAlerta {
    PENDENTE,
    EM_AGUARDO, //EM_AGUARDO sinaliza que está aguardando uma resposta do gerente.
    RESOLVIDO;

    @JsonCreator
    public static TipoStatusAlerta fromString(String value) {
        for (TipoStatusAlerta tipo : TipoStatusAlerta.values()) {
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
