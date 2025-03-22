package com.nectopoint.backend.modules.shared;

import java.time.Instant;

import com.nectopoint.backend.enums.TipoAviso;
import com.nectopoint.backend.enums.TipoStatusAlerta;

import lombok.Data;

@Data
public class WarningsStripped {
    private String id_aviso;
    private TipoAviso tipo_aviso;
    private Instant data_aviso;
    private TipoStatusAlerta status_aviso;
}
