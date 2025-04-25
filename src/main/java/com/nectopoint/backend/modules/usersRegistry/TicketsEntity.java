package com.nectopoint.backend.modules.usersRegistry;

import java.time.Instant;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.nectopoint.backend.enums.TipoAbono;
import com.nectopoint.backend.enums.TipoStatusTicket;
import com.nectopoint.backend.enums.TipoTicket;
import com.nectopoint.backend.modules.usersRegistry.PointRegistryEntity.Ponto;

import lombok.Data;

@Document(collection = "tickets")
@Data
public class TicketsEntity {
    @Id
    private String id_ticket;

    @Indexed
    private Long id_colaborador;
    private String nome_colaborador;
    private String cpf_colaborador;

    @Indexed
    private TipoTicket tipo_ticket;
    @Indexed
    private Instant data_ticket = Instant.now();
    @Indexed
    private TipoStatusTicket status_ticket = TipoStatusTicket.EM_AGUARDO;
    
    private Long id_gerente;
    private String nome_gerente;
    private String justificativa;

    // Usado para resolver tipo ALTERAR_PONTOS
    private List<Ponto> pontos_anterior;
    private List<Ponto> pontos_ajustado;
    private List<Instant> lista_horas;

    // Usado para PEDIR_FERIAS
    private Instant data_inicio_ferias;
    private Integer dias_ferias;

    // Usado para PEDIR_ABONO, informando dia ou dias de abono,
    // o intervalo de horas (00:00h às 23:59h caso seja um dia inteiro) e o motivo
    private TipoAbono motivo_abono;
    private List<Instant> dias_abono;

    private String mensagem;

    // Id do turno deve estar atrelado caso ticket seja
    // do tipo ALTERAR_PONTOS
    private String id_registro;
    // Id do aviso deve estar atrelado caso turno à ser alterado esteja IRREGULAR
    private String id_aviso;

    private String filePath;
}
