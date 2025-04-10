package com.nectopoint.backend.modules.shared;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import com.nectopoint.backend.enums.TipoStatusTurno;
import com.nectopoint.backend.modules.usersRegistry.PointRegistryEntity.Abono;
import com.nectopoint.backend.modules.usersRegistry.PointRegistryEntity.Ponto;

import lombok.Data;

@Data
public class PointRegistryStripped {
    private String id_registro = "inativo";

    private Instant inicio_turno;
    private Instant fim_turno;

    private TipoStatusTurno status_turno = TipoStatusTurno.NAO_INICIADO;

    private Long tempo_trabalhado_min = (long)0;
    private Boolean tirou_almoco = false;

    private List<Ponto> pontos_marcados = new ArrayList<>();

    private Abono abono;
}
