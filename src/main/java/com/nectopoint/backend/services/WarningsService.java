package com.nectopoint.backend.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nectopoint.backend.enums.TipoAviso;
import com.nectopoint.backend.modules.usersRegistry.PointRegistryEntity;
import com.nectopoint.backend.modules.usersRegistry.WarningsEntity;
import com.nectopoint.backend.repositories.WarningsRepository;

@Service
public class WarningsService {
    
    @Autowired
    private WarningsRepository warningsRepo;

    public WarningsEntity registerWarning(Long id_colaborador, TipoAviso tipo_aviso, List<PointRegistryEntity> pontos_marcados) {
        WarningsEntity data = new WarningsEntity();
        data.setId_colaborador(id_colaborador);
        data.setTipo_aviso(tipo_aviso);
        data.setPontos_marcados(pontos_marcados);
        return warningsRepo.save(data);
    }

}
