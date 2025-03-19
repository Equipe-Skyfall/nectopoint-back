package com.nectopoint.backend.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nectopoint.backend.enums.TipoStatus;
import com.nectopoint.backend.modules.usersRegistry.TicketsEntity;
import com.nectopoint.backend.repositories.TicketsRepository;

@Service
public class TicketsService {

    @Autowired
    private TicketsRepository ticketsRepo;

    public TicketsEntity changeStatus(String id_ticket, TipoStatus status_ticket) {
        TicketsEntity updateTarget = ticketsRepo.findById(id_ticket).get();
        updateTarget.setStatus_ticket(status_ticket);
        return ticketsRepo.save(updateTarget);
    }

    //Deletar TODOS os tickets de um usuário
    //ESSA FUNÇÃO É APENAS PARA SINCRONIZAR OS BANCOS DURANTE DESENVOLVIMENTO
    public void deleteAllByColaborador(Long id){
        List<TicketsEntity> tickets = ticketsRepo.findAllByIdColaborador(id);
        ticketsRepo.deleteAll(tickets);
    }
}
