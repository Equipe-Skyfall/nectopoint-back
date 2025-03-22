package com.nectopoint.backend.modules.shared;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;

import com.nectopoint.backend.enums.TipoPonto;
import com.nectopoint.backend.enums.TipoStatusTurno;
import com.nectopoint.backend.modules.usersRegistry.PointRegistryEntity;
import com.nectopoint.backend.modules.usersRegistry.PointRegistryEntity.Abono;

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

    @Data
    public static class Ponto {
        private TipoPonto tipo_ponto;
        private Instant data_hora;
        private Long tempo_entre_pontos;
        private Boolean almoco;
    }

    public PointRegistryEntity toPointRegistryEntity(Long id_colaborador, String nome_colaborador) {
        ModelMapper modelMapper = new ModelMapper();
        PointRegistryEntity entity = modelMapper.map(this, PointRegistryEntity.class);
        entity.setId_colaborador(id_colaborador);
        entity.setNome_colaborador(nome_colaborador);
        return entity;
    }
}
