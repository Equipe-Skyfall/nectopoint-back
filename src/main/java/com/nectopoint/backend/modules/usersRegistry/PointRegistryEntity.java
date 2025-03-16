package com.nectopoint.backend.modules.usersRegistry;

import java.time.Instant;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.nectopoint.backend.enums.TipoPonto;

import lombok.Data;

@Document(collection = "pontos")
@Data
public class PointRegistryEntity {

    @Id
    private String id_ponto;

    @Indexed
    private Long id_colaborador;

    private TipoPonto tipo_ponto;
    @Indexed
    private Instant data_hora = Instant.now();
    private Long horas_trabalhadas;
}
