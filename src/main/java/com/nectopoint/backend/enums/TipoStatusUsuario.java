package com.nectopoint.backend.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum TipoStatusUsuario {
    ONLINE,
    OFFLINE;

    @JsonCreator
    public static TipoStatusUsuario fromString(String value) {
        for (TipoStatusUsuario tipo : TipoStatusUsuario.values()) {
            if (tipo.name().equalsIgnoreCase(value)) {
                return tipo;
            }
        }
        throw new IllegalArgumentException("Valor para tipo_status_usuario inválido: " + value);
    }

    @JsonValue
    public String toValue() {
        return name();
    }
}

// ESSE ENUM AINDA NÃO ESTÁ SENDO USADO ! ! !
// Criei para diferenciar do Enum "TipoStatus"