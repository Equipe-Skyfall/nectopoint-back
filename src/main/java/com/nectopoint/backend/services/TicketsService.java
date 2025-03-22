package com.nectopoint.backend.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nectopoint.backend.dtos.TicketAnswerDTO;
import com.nectopoint.backend.dtos.TicketDTO;
import com.nectopoint.backend.enums.TipoStatusAlerta;
import com.nectopoint.backend.enums.TipoStatusTicket;
import com.nectopoint.backend.modules.user.UserSessionEntity;
import com.nectopoint.backend.modules.usersRegistry.TicketsEntity;
import com.nectopoint.backend.modules.usersRegistry.WarningsEntity;
import com.nectopoint.backend.repositories.tickets.TicketsRepository;
import com.nectopoint.backend.repositories.userSession.UserSessionRepository;

@Service
public class TicketsService {

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

    public TicketsEntity postTicket(Long id_colaborador, TicketDTO ticketDTO) {
        UserSessionEntity posterUser = userSessionRepo.findByColaborador(id_colaborador);

        TicketsEntity newTicket = ticketDTO.toTicketsEntity();

        newTicket.setId_colaborador(id_colaborador);
        newTicket.setNome_colaborador(posterUser.getDados_usuario().getNome());
        newTicket.setCpf_colaborador(posterUser.getDados_usuario().getCpf());
        TicketsEntity postedTicket = ticketsRepo.save(newTicket);

        posterUser.getTickets_usuario().add(postedTicket.toTicketsStripped());
        
        if (postedTicket.getId_aviso() != null) {
            WarningsEntity warning = warningsService.changeStatus(postedTicket.getId_aviso(), TipoStatusAlerta.EM_AGUARDO);
            posterUser.updateWarning(warning.toWarningsStripped());
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
                colaborador.updateWarning(warning.toWarningsStripped());
            }
            colaborador.updateTicket(ticket.toTicketsStripped());

            userSessionRepo.save(colaborador);
        } else {
            switch (ticket.getTipo_ticket()) {
                case PONTOS_IMPAR:
                    registryService.correctPointPunch(ticket.getId_registro(), ticket.getHorario_saida());
                    warning = warningsService.changeStatus(ticket.getId_aviso(), TipoStatusAlerta.RESOLVIDO);

                    colaborador = userSessionRepo.findByColaborador(ticket.getId_colaborador());
                    colaborador.removeWarning(warning.toWarningsStripped());
                    colaborador.updateTicket(ticket.toTicketsStripped());

                    userSessionRepo.save(colaborador);
                    break;
                case SEM_ALMOCO:
                    registryService.addLunchTime(ticket.getId_registro(), ticket.getInicio_intervalo(), ticket.getFim_intervalo());
                    warning = warningsService.changeStatus(ticket.getId_aviso(), TipoStatusAlerta.RESOLVIDO);

                    colaborador = userSessionRepo.findByColaborador(ticket.getId_colaborador());
                    colaborador.removeWarning(warning.toWarningsStripped());
                    colaborador.updateTicket(ticket.toTicketsStripped());

                    userSessionRepo.save(colaborador);
                    break;
                case PEDIR_FERIAS:
                    userSessionService.approveVacation(ticket.getId_colaborador(), ticket.getData_inicio_ferias(), ticket.getDias_ferias());

                    colaborador = userSessionRepo.findByColaborador(ticket.getId_colaborador());
                    colaborador.updateTicket(ticket.toTicketsStripped());

                    userSessionRepo.save(colaborador);
                    break;
                case PEDIR_ABONO:
                    registryService.processExcusedAbsence(ticket.getId_colaborador(), ticket.getMotivo_abono(), ticket.getDias_abono(), ticket.getAbono_inicio(), ticket.getAbono_final());
                    
                    colaborador = userSessionRepo.findByColaborador(ticket.getId_colaborador());
                    colaborador.updateTicket(ticket.toTicketsStripped());

                    userSessionRepo.save(colaborador);
                    break;
            }
        }
    }

    //Deletar TODOS os tickets de um usuário
    //ESSA FUNÇÃO É APENAS PARA SINCRONIZAR OS BANCOS DURANTE DESENVOLVIMENTO
    public void deleteAllByColaborador(Long id){
        List<TicketsEntity> tickets = ticketsRepo.findAllByIdColaborador(id);
        ticketsRepo.deleteAll(tickets);
    }
}
