package com.nectopoint.backend.modules.usersRegistry;

import java.time.Instant;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.nectopoint.backend.enums.TipoAviso;
import com.nectopoint.backend.enums.TipoStatus;

import lombok.Data;

@Document(collection = "avisos")
@Data
public class WarningsEntity {
    @Id
    private String id_aviso;
    @Indexed
    private Long id_colaborador;
    @Indexed
    private TipoAviso tipo_aviso;
    @Indexed
    private Instant data_aviso = Instant.now();
    @Indexed
    private TipoStatus status_aviso = TipoStatus.PENDENDE;

    private String mensagem;
    private List<PointRegistryEntity> pontos_marcados;
}
