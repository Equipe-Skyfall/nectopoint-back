package com.nectopoint.backend.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nectopoint.backend.enums.TipoAviso;
import com.nectopoint.backend.enums.TipoStatus;
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

    public void changeStatus(String id_aviso, TipoStatus status_aviso) {
        WarningsEntity updateTarget = warningsRepo.findById(id_aviso).get();
        updateTarget.setStatus_aviso(status_aviso);
        warningsRepo.save(updateTarget);
    }

    //Deletar TODOS os registros de alerta de um usuário
    //ESSA FUNÇÃO É APENAS PARA SINCRONIZAR OS BANCOS DURANTE DESENVOLVIMENTO
    public void deleteAllByColaborador(Long id) {
        List<WarningsEntity> warnings = warningsRepo.findAllByIdColaborador(id);
        warningsRepo.deleteAll(warnings);
    }

}
