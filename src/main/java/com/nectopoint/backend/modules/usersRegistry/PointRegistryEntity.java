package com.nectopoint.backend.modules.usersRegistry;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.nectopoint.backend.enums.TipoPonto;
import com.nectopoint.backend.enums.TipoStatusTurno;
import com.nectopoint.backend.modules.shared.PointRegistryStripped;

import lombok.Data;

@Document(collection = "turnos")
@Data
public class PointRegistryEntity {

    @Id
    private String id_registro;
    @Indexed
    private Long id_colaborador;
    @Indexed
    private Instant inicio_turno;

    private String nome_colaborador;

    private TipoStatusTurno status_turno;

    private Long tempo_trabalhado_min = (long)0;
    private Long tempo_intervalo_min = (long)0;

    private List<Ponto> pontos_marcados = new ArrayList<>();

    @Data
    public static class Ponto {
        private TipoPonto tipo_ponto;
        private Instant data_hora;
        private Long tempo_entre_pontos;
    }

    public PointRegistryStripped toPointRegistryStripped() {
        ModelMapper modelMapper = new ModelMapper();
        return modelMapper.map(this, PointRegistryStripped.class);
    }

    public PointRegistryEntity(Long id_colaborador, String nome_colaborador) {
        this.id_colaborador = id_colaborador;
        this.nome_colaborador = nome_colaborador;
    }
}