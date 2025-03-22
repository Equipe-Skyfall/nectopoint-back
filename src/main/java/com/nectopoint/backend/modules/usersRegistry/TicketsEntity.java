package com.nectopoint.backend.modules.usersRegistry;

import java.time.Instant;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.nectopoint.backend.enums.TipoStatusTicket;
import com.nectopoint.backend.enums.TipoTicket;
import com.nectopoint.backend.modules.shared.TicketsStripped;

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

    // Usado para resolver tipo PONTOS_IMPAR
    private Instant horario_saida;

    // Usado para resolver tipo SEM_ALMOCO
    private Instant inicio_intervalo;
    private Instant fim_intervalo;

    // Usado para PEDIR_FERIAS
    private Instant data_inicio_ferias;
    private Integer dias_ferias;

    // Usado para PEDIR_ABONO, informando dia ou dias de abono,
    // o intervalo de horas (00:00h às 23:59h caso seja um dia inteiro) e o motivo
    private String motivo_abono;
    private List<Instant> dias_abono;
    private Instant abono_inicio;
    private Instant abono_final;

    private String mensagem;

    // Ids de turno e alertas devem estar atrelados caso ticket seja
    // do tipo PONTOS_IMPAR ou SEM_ALMOCO
    private String id_registro;
    private String id_aviso;

    public TicketsStripped toTicketsStripped() {
        ModelMapper modelMapper = new ModelMapper();
        return modelMapper.map(this, TicketsStripped.class);
    }
}
