package com.nectopoint.backend.modules.user;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.nectopoint.backend.enums.TipoCargo;
import com.nectopoint.backend.modules.shared.WarningsSummary;
import com.nectopoint.backend.modules.usersRegistry.PointRegistryEntity;

import lombok.Data;

@Document(collection = "sessao-usuario")
@Data
public class UserSessionEntity {
    @Id
    private String id_sessao;
    private Long id_colaborador;
    private DadosUsuario dados_usuario = new DadosUsuario();
    private JornadaTrabalho jornada_trabalho = new JornadaTrabalho();
    private PointRegistryEntity jornada_atual;
    private List<PointRegistryEntity> jornadas_irregulares = new ArrayList<>();
    private List<WarningsSummary> alertas_usuario = new ArrayList<>();
    
    @Data
    public static class DadosUsuario {
        private String nome;
        private String cpf;
        private TipoCargo cargo;
        private String departamento;
        private String status;
    }
    
    @Data
    public static class JornadaTrabalho {
        private String tipo_jornada;
        private Long banco_de_horas;
        private Integer horas_diarias;
    }

    public UserSessionEntity(Long id_colaborador) {
        this.id_colaborador = id_colaborador;
        this.jornada_atual = new PointRegistryEntity(id_colaborador);
        this.jornada_atual.setId_registro("inativo");
    }

    public void missedWorkDay() {
        this.jornada_trabalho.banco_de_horas = this.jornada_trabalho.banco_de_horas - this.jornada_trabalho.horas_diarias;
    }
}
