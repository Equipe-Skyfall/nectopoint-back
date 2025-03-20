package com.nectopoint.backend.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nectopoint.backend.enums.TipoStatus;
import com.nectopoint.backend.modules.usersRegistry.TicketsEntity;
import com.nectopoint.backend.repositories.tickets.TicketsRepository;

@Service
public class TicketsService {

    @Autowired
    private TicketsRepository ticketsRepo;

    public TicketsEntity changeStatus(TicketsEntity ticket, TipoStatus status_ticket) {
        ticket.setStatus_ticket(status_ticket);
        if (status_ticket == TipoStatus.RESOLVIDO && ticket.getAviso_atrelado() != null) {
            ticket.getAviso_atrelado().setStatus_aviso(status_ticket);
        }
        return ticketsRepo.save(ticket);
    }

    //Deletar TODOS os tickets de um usuário
    //ESSA FUNÇÃO É APENAS PARA SINCRONIZAR OS BANCOS DURANTE DESENVOLVIMENTO
    public void deleteAllByColaborador(Long id){
        List<TicketsEntity> tickets = ticketsRepo.findAllByIdColaborador(id);
        ticketsRepo.deleteAll(tickets);
    }
}
