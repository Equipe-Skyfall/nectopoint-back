package com.nectopoint.backend.repositories.warnings;

import java.time.Instant;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.nectopoint.backend.enums.TipoAviso;
import com.nectopoint.backend.enums.TipoStatusAlerta;
import com.nectopoint.backend.modules.usersRegistry.WarningsEntity;

public interface WarningsRepositoryCustom {
    Page<WarningsEntity> findByParamsDynamic(Long id_colaborador, Instant start, Instant end,
                                               List<TipoStatusAlerta> status_aviso, TipoAviso tipo_aviso,
                                               Pageable pageable);
}
