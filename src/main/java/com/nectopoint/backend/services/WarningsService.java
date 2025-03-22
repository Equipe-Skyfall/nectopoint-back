package com.nectopoint.backend.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nectopoint.backend.enums.TipoAviso;
import com.nectopoint.backend.enums.TipoStatusAlerta;
import com.nectopoint.backend.modules.usersRegistry.WarningsEntity;
import com.nectopoint.backend.repositories.warnings.WarningsRepository;

@Service
public class WarningsService {
    
    @Autowired
    private WarningsRepository warningsRepo;

    public WarningsEntity registerWarning(Long id_colaborador, String nome_colaborador, String cpf_colaborador, TipoAviso tipo_aviso) {
        WarningsEntity warning = new WarningsEntity();

        warning.setId_colaborador(id_colaborador);
        warning.setNome_colaborador(nome_colaborador);
        warning.setCpf_colaborador(cpf_colaborador);
        warning.setTipo_aviso(tipo_aviso);

        return warningsRepo.save(warning);
    }
    
    public WarningsEntity changeStatus(String id_aviso, TipoStatusAlerta status_aviso) {
        WarningsEntity warning = warningsRepo.findById(id_aviso).get();
        warning.setStatus_aviso(status_aviso);
        
        if(status_aviso == TipoStatusAlerta.PENDENTE){
            warning.setId_ticket(null);
        }

        return warningsRepo.save(warning);
    }

    //Deletar TODOS os registros de alerta de um usuário
    //ESSA FUNÇÃO É APENAS PARA SINCRONIZAR OS BANCOS DURANTE DESENVOLVIMENTO
    public void deleteAllByColaborador(Long id) {
        List<WarningsEntity> warnings = warningsRepo.findAllByIdColaborador(id);
        warningsRepo.deleteAll(warnings);
    }

}
