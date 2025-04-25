package com.nectopoint.backend.controllers.registry;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.nectopoint.backend.dtos.ErrorMessageDTO;
import com.nectopoint.backend.dtos.TicketAnswerDTO;
import com.nectopoint.backend.dtos.TicketDTO;
import com.nectopoint.backend.dtos.TicketEntityDTO;
import com.nectopoint.backend.enums.TipoStatusTicket;
import com.nectopoint.backend.enums.TipoStatusUsuario;
import com.nectopoint.backend.enums.TipoTicket;
import com.nectopoint.backend.modules.user.UserSessionEntity;
import com.nectopoint.backend.modules.usersRegistry.TicketsEntity;
import com.nectopoint.backend.repositories.tickets.TicketsRepository;
import com.nectopoint.backend.repositories.userSession.UserSessionRepository;
import com.nectopoint.backend.services.TicketsService;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Valid;
import jakarta.validation.Validator;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;



@RestController
@RequestMapping("/tickets")
public class TicketsController {

    @Autowired
    private TicketsRepository ticketRepo;
    @Autowired
    private UserSessionRepository userSessionRepo;

    @Autowired
    private TicketsService ticketsService;

    @Autowired
    private Validator validator;

    @PostMapping(value = "/postar", consumes = {"multipart/form-data"})
    public ResponseEntity<?> postTicket(
            @RequestPart("ticket") TicketDTO ticketDTO,
            @RequestPart(value = "file", required = false) MultipartFile file,
            HttpServletRequest request
    ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication.getPrincipal() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Long id_colaborador = Long.parseLong(authentication.getPrincipal().toString());

        UserSessionEntity user = userSessionRepo.findByColaborador(id_colaborador);
        TipoStatusUsuario status = user.getDados_usuario().getStatus();

        ticketDTO.setStatus_usuario(status);

        Set<ConstraintViolation<TicketDTO>> violations = validator.validate(ticketDTO);
        if (!violations.isEmpty()) {
            List<ErrorMessageDTO> errorMessages = violations.stream()
                .map(violation -> new ErrorMessageDTO(
                    violation.getMessage(),
                    violation.getPropertyPath().toString()
                ))
                .collect(Collectors.toList());
    
            return ResponseEntity.badRequest().body(errorMessages);
        }

        if (file != null && !file.isEmpty()) {
            String contentType = file.getContentType();
            if (!isValidFileType(contentType)) {
                return ResponseEntity
                        .badRequest()
                        .body(new ErrorMessageDTO("Extensão do arquivo inválida. Verifique que está mandando um PNG, JPEG, ou PDF.", "file"));
            }
        }

        Optional<MultipartFile> optionalFile = (file != null && !file.isEmpty())
            ? Optional.of(file)
            : Optional.empty();

        return ResponseEntity.ok(ticketsService.postTicket(id_colaborador, ticketDTO, optionalFile));
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

    @GetMapping("/files/{ticketId}")
    public ResponseEntity<Resource> getFile(@PathVariable String ticketId) {
        TicketsEntity ticket = ticketRepo.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));

        String filePath = ticket.getFilePath();

        Path path = Paths.get(filePath);
        Resource resource = new FileSystemResource(path);

        if (!resource.exists()) {
            throw new RuntimeException("File not found");
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }
    
    @GetMapping("/listar")
    public ResponseEntity<Page<TicketEntityDTO>> getAllTickets(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "5") int size,
        @RequestParam(required = false) Instant startDate,
        @RequestParam(required = false) Instant endDate,
        @RequestParam(required = false) List<TipoStatusTicket> lista_status,
        @RequestParam(required = false) TipoTicket tipo_ticket,
        @RequestParam(required = false) String nome_colaborador
    ) {
        Pageable pageable = PageRequest.of(page, size);

        Page<TicketEntityDTO> ticketPage = ticketRepo.findByParamsDynamic(nome_colaborador, startDate, endDate, lista_status, tipo_ticket, pageable);

        return new ResponseEntity<>(ticketPage, HttpStatus.OK);
    }
    
    private boolean isValidFileType(String contentType) {
        return contentType != null && (
            contentType.equals("image/png") ||
            contentType.equals("image/jpeg") ||
            contentType.equals("application/pdf")
        );
    }
}
