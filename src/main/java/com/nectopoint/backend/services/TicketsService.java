package com.nectopoint.backend.services;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.nectopoint.backend.dtos.TicketAnswerDTO;
import com.nectopoint.backend.dtos.TicketDTO;
import com.nectopoint.backend.dtos.TicketDTO.Pares;
import com.nectopoint.backend.enums.TipoStatusAlerta;
import com.nectopoint.backend.enums.TipoStatusTicket;
import com.nectopoint.backend.enums.TipoStatusUsuario;
import com.nectopoint.backend.enums.TipoTicket;
import com.nectopoint.backend.modules.user.UserSessionEntity;
import com.nectopoint.backend.modules.usersRegistry.TicketsEntity;
import com.nectopoint.backend.modules.usersRegistry.WarningsEntity;
import com.nectopoint.backend.modules.usersRegistry.PointRegistryEntity.Ponto;
import com.nectopoint.backend.repositories.tickets.TicketsRepository;
import com.nectopoint.backend.repositories.userSession.UserSessionRepository;
import com.nectopoint.backend.utils.DataTransferHelper;
import com.nectopoint.backend.utils.DateTimeHelper;

@Service
public class TicketsService {

    private final DataTransferHelper dataTransferHelper;
    private final DateTimeHelper dateTimeHelper;

    @Autowired
    private TicketsRepository ticketsRepo;
    @Autowired
    private UserSessionRepository userSessionRepo;

    @Autowired
    private WarningsService warningsService;
    @Autowired
    private PointRegistryService registryService;
    @Autowired
    private UserSessionService userSessionService;

    public TicketsService(DataTransferHelper dataTransferHelper, DateTimeHelper dateTimeHelper) {
        this.dataTransferHelper = dataTransferHelper;
        this.dateTimeHelper = dateTimeHelper;
    }

    public TicketsEntity postTicket(Long id_colaborador, TicketDTO ticketDTO, Optional<MultipartFile> file) {
        UserSessionEntity posterUser = userSessionRepo.findByColaborador(id_colaborador);

        if (ticketDTO.getTipo_ticket().equals(TipoTicket.ALTERAR_PONTOS)) {
            List<Instant> time_list = buildTimeList(
                ticketDTO.getPontos_anterior().get(0).getData_hora(),
                ticketDTO.getPontos_ajustado(),
                ticketDTO.getNovos_pontos()
            );

            List<Ponto> final_pontos_ajustado = buildPointList(time_list);

            ticketDTO.setPontos_ajustado(final_pontos_ajustado);
            ticketDTO.setLista_horas(time_list);
        }

        TicketsEntity newTicket = dataTransferHelper.toTicketsEntity(ticketDTO);

        newTicket.setId_colaborador(id_colaborador);
        newTicket.setNome_colaborador(posterUser.getDados_usuario().getNome());
        newTicket.setCpf_colaborador(posterUser.getDados_usuario().getCpf());

        if (file.isPresent() && !file.get().isEmpty()) {
            try {
                String uploadDir = "uploads/tickets/";

                String originalFileName = file.get().getOriginalFilename();
                String uniqueFileName = System.currentTimeMillis() + "_" + originalFileName;

                Path path = Paths.get(uploadDir + uniqueFileName);
                Files.createDirectories(path.getParent());
                file.get().transferTo(path.toFile());

                newTicket.setFilePath(uploadDir + uniqueFileName);
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException("Error saving file: " + e.getMessage());
            }
        }

        TicketsEntity postedTicket = ticketsRepo.save(newTicket);

        posterUser.getTickets_usuario().add(dataTransferHelper.toTicketsStripped(postedTicket));
        
        if (postedTicket.getId_aviso() != null) {
            WarningsEntity warning = warningsService.changeStatus(postedTicket.getId_aviso(), TipoStatusAlerta.EM_AGUARDO);
            posterUser.updateWarning(dataTransferHelper.toWarningsStripped(warning));
        }

        userSessionRepo.save(posterUser);
        return postedTicket;
    }

    public void answerTicket(Long id_gerente, TicketAnswerDTO ticketAnswerDTO) {
        TicketsEntity ticket = ticketAnswerDTO.getTicket();
        UserSessionEntity gerente = userSessionRepo.findByColaborador(id_gerente);
        UserSessionEntity colaborador;
        WarningsEntity warning;

        TipoStatusTicket novo_status = ticketAnswerDTO.getNovo_status();
        
        ticket.setId_gerente(id_gerente);
        ticket.setNome_gerente(gerente.getDados_usuario().getNome());
        ticket.setStatus_ticket(novo_status);
        if ( novo_status == TipoStatusTicket.REPROVADO ) {
            ticket.setJustificativa(ticketAnswerDTO.getJustificativa());
            ticketsRepo.save(ticket);

            colaborador = userSessionRepo.findByColaborador(ticket.getId_colaborador());
            if (ticket.getId_aviso() != null) {
                warning = warningsService.changeStatus(ticket.getId_aviso(), TipoStatusAlerta.PENDENTE);
                colaborador.updateWarning(dataTransferHelper.toWarningsStripped(warning));
            }
            colaborador.updateTicket(dataTransferHelper.toTicketsStripped(ticket));

            userSessionRepo.save(colaborador);
        } else {
            ticketsRepo.save(ticket);
            switch (ticket.getTipo_ticket()) {
                case ALTERAR_PONTOS:
                    registryService.editShift(ticket.getId_registro(), ticket.getLista_horas());
                    
                    colaborador = userSessionRepo.findByColaborador(ticket.getId_colaborador());
                    if (ticket.getId_aviso() != null) {
                        warning = warningsService.changeStatus(ticket.getId_aviso(), TipoStatusAlerta.RESOLVIDO);
                        colaborador.removeWarning(dataTransferHelper.toWarningsStripped(warning));
                    }
                    colaborador.updateTicket(dataTransferHelper.toTicketsStripped(ticket));

                    userSessionRepo.save(colaborador);
                    break;
                case PEDIR_FERIAS:
                    userSessionService.approveVacation(ticket.getId_colaborador(), ticket.getData_inicio_ferias(), ticket.getDias_ferias());

                    colaborador = userSessionRepo.findByColaborador(ticket.getId_colaborador());
                    colaborador.updateTicket(dataTransferHelper.toTicketsStripped(ticket));

                    userSessionRepo.save(colaborador);
                    break;
                case PEDIR_ABONO:
                    registryService.processExcusedAbsence(ticket.getId_colaborador(), ticket.getMotivo_abono(), ticket.getDias_abono());
                    
                    colaborador = userSessionRepo.findByColaborador(ticket.getId_colaborador());
                    colaborador.updateTicket(dataTransferHelper.toTicketsStripped(ticket));

                    userSessionRepo.save(colaborador);
                    break;
                case SOLICITAR_FOLGA:
                    colaborador = userSessionRepo.findByColaborador(ticket.getId_colaborador());
                    colaborador.updateTicket(dataTransferHelper.toTicketsStripped(ticket));

                    userSessionRepo.save(colaborador);
                case PEDIR_HORA_EXTRA:
                    colaborador = userSessionRepo.findByColaborador(ticket.getId_colaborador());
                    colaborador.getDados_usuario().setStatus(TipoStatusUsuario.ESCALADO);
                    colaborador.updateTicket(dataTransferHelper.toTicketsStripped(ticket));

                    userSessionRepo.save(colaborador);
            }
        }
    }

    private List<Instant> buildTimeList(Instant shiftDay, List<Ponto> pontos_ajustado, List<Pares> pares_pontos) {
        List<Instant> time_list = new ArrayList<>();

        for (Ponto ponto : pontos_ajustado) {
            time_list.add(dateTimeHelper.joinDateTime(shiftDay, ponto.getData_hora()));
        }
        for (Pares par : pares_pontos) {
            time_list.add(dateTimeHelper.joinDateTime(shiftDay, par.getHorario_saida()));
            time_list.add(dateTimeHelper.joinDateTime(shiftDay, par.getHorario_entrada()));
        }

        return time_list.stream().sorted().toList();
    }

    private List<Ponto> buildPointList(List<Instant> time_list) {
        List<Ponto> lista_pontos = new ArrayList<>();

        for (int i = 0; i < time_list.size()-1; i++) {
            if (i == 0) {
                lista_pontos.add(registryService.processNewEntry(null, time_list.get(i)));
            } else {
                lista_pontos.add(registryService.processNewEntry(lista_pontos.get(i-1), time_list.get(i)));
            }
        }

        return lista_pontos;
    }

    //Deletar TODOS os tickets de um usuário
    //ESSA FUNÇÃO É APENAS PARA SINCRONIZAR OS BANCOS DURANTE DESENVOLVIMENTO
    public void deleteAllByColaborador(Long id){
        List<TicketsEntity> tickets = ticketsRepo.findAllByIdColaborador(id);
        ticketsRepo.deleteAll(tickets);
    }
}
