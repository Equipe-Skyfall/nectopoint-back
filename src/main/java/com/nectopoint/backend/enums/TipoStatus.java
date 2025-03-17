package com.nectopoint.backend.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum TipoStatus {
    PENDENDE,
    EM_AGUARDO, //EM_AGUARDO sinaliza que está aguardando uma resposta do gerente.
    RESOLVIDO;

    @JsonCreator
    public static TipoStatus fromString(String value) {
        for (TipoStatus tipo : TipoStatus.values()) {
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

// Diferente de "TipoStatusUsuario" esse Enum será aplicado aos Tickets e Alertas