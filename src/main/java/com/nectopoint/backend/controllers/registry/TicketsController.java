package com.nectopoint.backend.controllers.registry;

import java.time.Instant;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nectopoint.backend.dtos.TicketDTO;
import com.nectopoint.backend.modules.usersRegistry.TicketsEntity;
import com.nectopoint.backend.repositories.TicketsRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;



@RestController
@RequestMapping("/tickets")
public class TicketsController {
    
    @Autowired
    private TicketsRepository ticketRepo;

    @PostMapping("/postar")
    public TicketsEntity postTicket(@RequestBody TicketDTO requestData) {
        TicketsEntity new_ticket = new TicketsEntity();
        new_ticket.setId_colaborador(requestData.getId_colaborador());
        new_ticket.setTipo_ticket(requestData.getTipo_ticket());

        new_ticket.setMensagem(Optional.ofNullable(requestData.getMensagem()).orElse(null));
        new_ticket.setId_aviso(Optional.ofNullable(requestData.getId_aviso()).orElse(null));

        return ticketRepo.save(new_ticket);
    }
    

    @GetMapping("/")
    public TicketsEntity getTicketById(@RequestParam String id) {
        return ticketRepo.findById(id).get();
    }
    
    @GetMapping("/tickets-todos")
    public ResponseEntity<Page<TicketsEntity>> getAllTickets(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "5") int size,
        @RequestParam(required = false) Instant startDate,
        @RequestParam(required = false) Instant endDate
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<TicketsEntity> ticketPage;
        if (startDate != null && endDate != null) {
            ticketPage = ticketRepo.findAllByDate(startDate, endDate, pageable);
        } else {
            ticketPage = ticketRepo.findAll(pageable);
        }

        return new ResponseEntity<>(ticketPage, HttpStatus.OK);
    }
    
    @GetMapping("/tickets-usuario")
    public ResponseEntity<Page<TicketsEntity>> getUserTickets(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "5") int size,
        @RequestParam(required = false) Instant startDate,
        @RequestParam(required = false) Instant endDate,
        @RequestParam Long id_colaborador
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<TicketsEntity> ticketPage;
        if (startDate != null && endDate != null) {
            ticketPage = ticketRepo.findByIdColaboradorAndDate(id_colaborador, startDate, endDate, pageable);
        } else {
            ticketPage = ticketRepo.findByIdColaborador(id_colaborador, pageable);
        }

        return new ResponseEntity<>(ticketPage, HttpStatus.OK);
    }
    
}
