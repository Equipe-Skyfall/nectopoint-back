package com.nectopoint.backend.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nectopoint.backend.enums.TipoAviso;
import com.nectopoint.backend.enums.TipoStatus;
import com.nectopoint.backend.modules.shared.WarningsSummary;
import com.nectopoint.backend.modules.user.UserSessionEntity;
import com.nectopoint.backend.modules.usersRegistry.PointRegistryEntity;
import com.nectopoint.backend.modules.usersRegistry.WarningsEntity;
import com.nectopoint.backend.repositories.UserSessionRepository;
import com.nectopoint.backend.repositories.WarningsRepository;

@Service
public class WarningsService {
    
    @Autowired
    private WarningsRepository warningsRepo;
    @Autowired
    private UserSessionRepository userSessionRepo;

    public void registerWarning(Long id_colaborador, TipoAviso tipo_aviso, Optional<String> mensagem, Optional<PointRegistryEntity> turno_irregular) {
        WarningsEntity warning = new WarningsEntity();

        warning.setId_colaborador(id_colaborador);
        warning.setTipo_aviso(tipo_aviso);
        if (mensagem.isPresent()) {
            warning.setMensagem(mensagem.get());
        }
        if (turno_irregular.isPresent()) {
            warning.setTurno_irregular(turno_irregular.get());
        }
        
        syncWithUserAdd(id_colaborador, warning);
        warningsRepo.save(warning);
    }
    
    public void changeStatus(String id_aviso, TipoStatus status_aviso) {
        WarningsEntity updateTarget = warningsRepo.findById(id_aviso).get();
        updateTarget.setStatus_aviso(status_aviso);

        syncWithUserStatus(updateTarget.getId_colaborador(), id_aviso, status_aviso);
        warningsRepo.save(updateTarget);
    }
    
    private void syncWithUserAdd(Long id_colaborador, WarningsEntity warning) {
        UserSessionEntity user = userSessionRepo.findByColaborador(id_colaborador);
        
        WarningsSummary warning_summary = new WarningsSummary();
        warning_summary.setId_aviso(warning.getId_aviso());
        warning_summary.setData_aviso(warning.getData_aviso());
        warning_summary.setStatus_aviso(warning.getStatus_aviso());
        warning_summary.setTipo_aviso(warning.getTipo_aviso());

        user.getAlertas_usuario().add(warning_summary);
        userSessionRepo.save(user);
    }

    private void syncWithUserStatus(Long id_colaborador, String id_aviso, TipoStatus status_aviso) {
        UserSessionEntity user = userSessionRepo.findByColaborador(id_colaborador);

        WarningsSummary warning_summary = user.getAlertas_usuario().stream()
                                         .filter(w -> w.getId_aviso().equals(id_aviso))
                                         .findFirst().get();
        if (status_aviso == TipoStatus.RESOLVIDO) {
            user.getAlertas_usuario().remove(warning_summary);
        } else {
            warning_summary.setStatus_aviso(status_aviso);
        }

        userSessionRepo.save(user);
    }

    //Deletar TODOS os registros de alerta de um usuário
    //ESSA FUNÇÃO É APENAS PARA SINCRONIZAR OS BANCOS DURANTE DESENVOLVIMENTO
    public void deleteAllByColaborador(Long id) {
        List<WarningsEntity> warnings = warningsRepo.findAllByIdColaborador(id);
        warningsRepo.deleteAll(warnings);
    }

}
