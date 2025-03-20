package com.nectopoint.backend.controllers.registry;

import java.time.Instant;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nectopoint.backend.dtos.TicketDTO;
import com.nectopoint.backend.enums.TipoStatus;
import com.nectopoint.backend.enums.TipoTicket;
import com.nectopoint.backend.modules.usersRegistry.TicketsEntity;
import com.nectopoint.backend.repositories.tickets.TicketsRepository;
import com.nectopoint.backend.services.WarningsService;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;



@RestController
@RequestMapping("/tickets")
public class TicketsController {
    
    @Autowired
    private TicketsRepository ticketRepo;

    @Autowired
    private WarningsService warningsService;

    @PostMapping("/postar")
    public ResponseEntity<TicketsEntity> postTicket(@Valid @RequestBody TicketDTO requestData) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication.getPrincipal() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Long id_colaborador = Long.parseLong(authentication.getPrincipal().toString());
        TicketsEntity new_ticket = new TicketsEntity();

        new_ticket.setId_colaborador(id_colaborador);
        new_ticket.setTipo_ticket(requestData.getTipo_ticket());

        new_ticket.setMensagem(Optional.ofNullable(requestData.getMensagem()).orElse(null));
        if (requestData.getAviso_atrelado() != null) {
            new_ticket.setAviso_atrelado(requestData.getAviso_atrelado());
            warningsService.changeStatus(requestData.getAviso_atrelado(), TipoStatus.EM_AGUARDO);
        }

        return ResponseEntity.ok(ticketRepo.save(new_ticket));
    }
    

    @GetMapping("/{id}")
    public TicketsEntity getTicketById(@PathVariable String id) {
        return ticketRepo.findById(id).get();
    }
    
    @GetMapping("/listar")
    public ResponseEntity<Page<TicketsEntity>> getAllTickets(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "5") int size,
        @RequestParam(required = false) Instant startDate,
        @RequestParam(required = false) Instant endDate,
        @RequestParam(required = false) TipoStatus statusTicket,
        @RequestParam(required = false) TipoTicket tipoTicket,
        @RequestParam(required = false) Long id_colaborador
    ) {
        Pageable pageable = PageRequest.of(page, size);

        Page<TicketsEntity> ticketPage = ticketRepo.findByParamsDynamic(id_colaborador, startDate, endDate, statusTicket, tipoTicket, pageable);

        return new ResponseEntity<>(ticketPage, HttpStatus.OK);
    }
    
}
