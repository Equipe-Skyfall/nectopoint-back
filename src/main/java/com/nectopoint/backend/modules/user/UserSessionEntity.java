package com.nectopoint.backend.modules.user;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.nectopoint.backend.enums.TipoCargo;
import com.nectopoint.backend.enums.TipoStatusUsuario;
import com.nectopoint.backend.modules.shared.PointRegistryStripped;
import com.nectopoint.backend.modules.shared.WarningsStripped;

import lombok.Data;

@Document(collection = "sessao-usuario")
@Data
public class UserSessionEntity {
    @Id
    private String id_sessao;
    private Long id_colaborador;
    private DadosUsuario dados_usuario;
    private JornadaTrabalho jornada_trabalho;
    private PointRegistryStripped jornada_atual = new PointRegistryStripped();
    private List<PointRegistryStripped> jornadas_historico = new ArrayList<>();
    private List<PointRegistryStripped> jornadas_irregulares = new ArrayList<>();
    private List<WarningsStripped> alertas_usuario = new ArrayList<>();
    
    @Data
    public static class DadosUsuario {
        private String nome;
        private String cpf;
        private TipoCargo cargo;
        private String departamento;
        private TipoStatusUsuario status;

        public DadosUsuario(String nome, String cpf, TipoCargo cargo, String departamento) {
            this.nome = nome;
            this.cpf = cpf;
            this.cargo = cargo;
            this.departamento = departamento;
            this.status = TipoStatusUsuario.TRABALHANDO;
        }
    }
    
    @Data
    public static class JornadaTrabalho {
        private String tipo_jornada;
        private Long banco_de_horas;
        private Integer horas_diarias;

        public JornadaTrabalho(String tipo_jornada, Long banco_de_horas, Integer horas_diarias) {
            this.tipo_jornada = tipo_jornada;
            this.banco_de_horas = banco_de_horas;
            this.horas_diarias = horas_diarias;
        }
    }

    public void missedWorkDay() {
        this.jornada_trabalho.banco_de_horas = this.jornada_trabalho.banco_de_horas - this.jornada_trabalho.horas_diarias;
    }
}
