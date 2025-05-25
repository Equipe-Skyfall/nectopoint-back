package com.nectopoint.backend.modules.user;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.nectopoint.backend.enums.TipoCargo;
import com.nectopoint.backend.enums.TipoEscala;
import com.nectopoint.backend.enums.TipoStatusUsuario;
import com.nectopoint.backend.modules.shared.PointRegistryStripped;
import com.nectopoint.backend.modules.shared.TicketsStripped;
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

    private List<TicketsStripped> tickets_usuario = new ArrayList<>();
    private List<WarningsStripped> alertas_usuario = new ArrayList<>();

   public void setAlertas_usuario(List<WarningsStripped> newWarnings) {
    this.alertas_usuario = newWarnings; // ✅ CORRETO
    // ❌ REMOVER: throw new UnsupportedOperationException("Not supported yet.");
}

    // Removed duplicate getAlertas_usuario() method to resolve compilation error.
    
    @Data
    public static class DadosUsuario {
        private String nome;
        private String cpf;
        private TipoCargo cargo;
        private String departamento;
        private TipoStatusUsuario status = TipoStatusUsuario.ESCALADO;
        private Instant ferias_inicio;
        private Instant ferias_final;
    }
    
    @Data
    public static class JornadaTrabalho {
        private Long banco_de_horas;
        private Integer horas_diarias;
        private TipoEscala tipo_escala;
    }

    public void updateRegistry(PointRegistryStripped registryStripped) {
        PointRegistryStripped target = jornadas_historico.stream()
            .filter(registry -> registry.getId_registro().equals(registryStripped.getId_registro()))
            .findFirst().get();

        jornadas_historico.set(jornadas_historico.indexOf(target), registryStripped);
    }

    public void updateTicket(TicketsStripped ticketStripped) {
        TicketsStripped target = tickets_usuario.stream()
        .filter(ticket -> ticket.getId_ticket().equals(ticketStripped.getId_ticket()))
        .findFirst().get();

        tickets_usuario.set(tickets_usuario.indexOf(target), ticketStripped);
    }

    public void updateWarning(WarningsStripped warningsStripped) {
        WarningsStripped target = alertas_usuario.stream()
        .filter(warning -> warning.getId_aviso().equals(warningsStripped.getId_aviso()))
        .findFirst().get();

        alertas_usuario.set(alertas_usuario.indexOf(target), warningsStripped);
    }
    public void updateUserWarnings(List<WarningsStripped> warningsStripped) {
        for (WarningsStripped warning : warningsStripped) {
            WarningsStripped target = alertas_usuario.stream()
            .filter(w -> w.getId_aviso().equals(warning.getId_aviso()))
            .findFirst().get();
            alertas_usuario.set(alertas_usuario.indexOf(target), warning);
        }
    }
    public List<WarningsStripped> getAlertas_usuario(){
        return (alertas_usuario);
    }
    public void removeWarning(WarningsStripped warningsStripped) {
        alertas_usuario.remove(warningsStripped);
    }

    public void missedWorkDay() {
        this.jornada_trabalho.banco_de_horas = this.jornada_trabalho.banco_de_horas - this.jornada_trabalho.horas_diarias;
    }
}
