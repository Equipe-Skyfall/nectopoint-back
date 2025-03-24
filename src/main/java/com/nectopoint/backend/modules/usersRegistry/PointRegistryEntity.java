package com.nectopoint.backend.modules.usersRegistry;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.nectopoint.backend.enums.TipoAbono;
import com.nectopoint.backend.enums.TipoPonto;
import com.nectopoint.backend.enums.TipoStatusTurno;

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
    private Instant fim_turno;

    private String nome_colaborador;
    private String cpf_colaborador;

    private TipoStatusTurno status_turno;

    private Long tempo_trabalhado_min = (long)0;
    private Boolean tirou_almoco = false;

    private List<Ponto> pontos_marcados = new ArrayList<>();

    private Abono abono;

    private String id_aviso;

    @Data
    public static class Ponto {
        private TipoPonto tipo_ponto;
        private Instant data_hora;
        private Long tempo_entre_pontos;
        private Boolean almoco;
    }

    @Data
    public static class Abono {
        TipoAbono motivo_abono;
        String horarios_abono;
    }
    
    public void sortPontos() {
        pontos_marcados.sort(Comparator.comparing(Ponto::getData_hora));
    }
}