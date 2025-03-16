package com.nectopoint.backend.modules.usersRegistry;

import java.time.Instant;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.nectopoint.backend.enums.TipoStatus;
import com.nectopoint.backend.enums.TipoTicket;

import jakarta.persistence.Id;
import lombok.Data;

@Document(collection = "tickets")
@Data
public class TicketsEntity {
    @Id
    private String id_ticket;
    @Indexed
    private Long id_colaborador;
    @Indexed
    private TipoTicket tipo_ticket;
    @Indexed
    private Instant data_ticket = Instant.now();
    @Indexed
    private TipoStatus status_ticket = TipoStatus.EM_AGUARDO;
    
    private String id_aviso;

    private String mensagem;
}
