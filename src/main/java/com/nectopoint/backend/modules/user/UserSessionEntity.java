package com.nectopoint.backend.modules.user;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.nectopoint.backend.enums.TipoCargo;
import com.nectopoint.backend.enums.TipoPonto;
import com.nectopoint.backend.modules.shared.WarningsSummary;

import lombok.Data;

@Document(collection = "sessao-usuario")
@Data
public class UserSessionEntity {
    @Id
    private String id_sessao;
    private Long id_colaborador;
    private DadosUsuario dados_usuario = new DadosUsuario();
    private JornadaTrabalho jornada_trabalho = new JornadaTrabalho();
    private List<WarningsSummary> alertas_usuario = new ArrayList<>();

    @Data
    public static class DadosUsuario {
        private TipoCargo cargo;
        private String departamento;
        private String status;
    }

    @Data
    public static class JornadaTrabalho {
        private String tipo_jornada;
        private Float banco_de_horas;
        private Integer horas_diarias;
        private JornadaAtual jornada_atual = new JornadaAtual();
    }

    @Data
    public static class JornadaAtual {
        private TipoPonto batida_atual = TipoPonto.ENTRADA;
        private Instant ultima_entrada;
    }
}
