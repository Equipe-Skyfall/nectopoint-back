package com.nectopoint.backend.controllers.registry;

import java.time.Instant;
import java.util.List;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.nectopoint.backend.providers.JWTProvider;
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

import com.nectopoint.backend.dtos.TicketAnswerDTO;
import com.nectopoint.backend.dtos.TicketDTO;
import com.nectopoint.backend.enums.TipoStatusTicket;
import com.nectopoint.backend.enums.TipoStatusUsuario;
import com.nectopoint.backend.enums.TipoTicket;
import com.nectopoint.backend.modules.usersRegistry.TicketsEntity;
import com.nectopoint.backend.repositories.tickets.TicketsRepository;
import com.nectopoint.backend.services.TicketsService;

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
    private JWTProvider jwtProvider;

    @Autowired
    private TicketsRepository ticketRepo;

    @Autowired
    private TicketsService ticketsService;

    @PostMapping("/postar")
    public ResponseEntity<TicketsEntity> postTicket(@Valid @RequestBody TicketDTO ticketDTO, 
    HttpServletRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication.getPrincipal() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Long id_colaborador = Long.parseLong(authentication.getPrincipal().toString());
        //pega o cookie
        Cookie[] cookies = request.getCookies();
        String token = null;
        //tira o jwt do cookie
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("jwt_token".equals(cookie.getName())) {
                    token = cookie.getValue();
                    break;
                }
            }
        }
        if (token == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        // descriptografa o cookie
        DecodedJWT decodedToken = jwtProvider.validateToken(token);
        // pega o status do cookie
        String statusStr = decodedToken.getClaim("status").asString();
        TipoStatusUsuario status = TipoStatusUsuario.valueOf(statusStr);

        ticketDTO.setStatus_usuario(status);

        return ResponseEntity.ok(ticketsService.postTicket(id_colaborador, ticketDTO));
    }

    @PostMapping("/responder")
    public ResponseEntity<String> answerTicket(@Valid @RequestBody TicketAnswerDTO ticketAnswer) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication.getPrincipal() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Long id_gerente = Long.parseLong(authentication.getPrincipal().toString());

        ticketsService.answerTicket(id_gerente, ticketAnswer);
        return ResponseEntity.ok("Resposta enviada com sucesso!");
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
        @RequestParam(required = false) List<TipoStatusTicket> lista_status,
        @RequestParam(required = false) TipoTicket tipo_ticket,
        @RequestParam(required = false) String nome_colaborador
    ) {
        Pageable pageable = PageRequest.of(page, size);

        Page<TicketsEntity> ticketPage = ticketRepo.findByParamsDynamic(nome_colaborador, startDate, endDate, lista_status, tipo_ticket, pageable);

        return new ResponseEntity<>(ticketPage, HttpStatus.OK);
    }
    
}
