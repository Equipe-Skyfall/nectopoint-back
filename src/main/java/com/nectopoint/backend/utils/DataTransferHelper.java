package com.nectopoint.backend.utils;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import com.nectopoint.backend.dtos.TicketDTO;
import com.nectopoint.backend.dtos.TicketEntityDTO;
import com.nectopoint.backend.dtos.UserDetailsDTO;
import com.nectopoint.backend.dtos.UserSessionDTO;
import com.nectopoint.backend.modules.shared.PointRegistryStripped;
import com.nectopoint.backend.modules.shared.TicketsStripped;
import com.nectopoint.backend.modules.shared.WarningsStripped;
import com.nectopoint.backend.modules.user.UserEntity;
import com.nectopoint.backend.modules.user.UserSessionEntity;
import com.nectopoint.backend.modules.usersRegistry.PointRegistryEntity;
import com.nectopoint.backend.modules.usersRegistry.TicketsEntity;
import com.nectopoint.backend.modules.usersRegistry.WarningsEntity;

@Component
public class DataTransferHelper {
    
    private final ModelMapper modelMapper;

    public DataTransferHelper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public UserDetailsDTO toUserDetailsDTO(UserEntity user) {
        return modelMapper.map(user, UserDetailsDTO.class);
    }

    public UserSessionEntity toUserSessionEntity(UserDetailsDTO userDetails) {
        return modelMapper.map(userDetails, UserSessionEntity.class);
    }

    public UserSessionEntity toUserSessionEntityUpdate(UserSessionEntity targetEntity, UserDetailsDTO userDetails) {
        modelMapper.map(userDetails, targetEntity);
        return targetEntity;
    }

    public UserSessionDTO toUserSessionDTO(UserSessionEntity userSession) {
        return modelMapper.map(userSession, UserSessionDTO.class);
    }

    public PointRegistryStripped toPointRegistryStripped(PointRegistryEntity registry) {
        return modelMapper.map(registry, PointRegistryStripped.class);
    }

    public PointRegistryEntity toPointRegistryEntity(Long id_colaborador, String nome_colaborador, String cpf_colaborador, PointRegistryStripped registryStripped) {
        PointRegistryEntity entity = modelMapper.map(registryStripped, PointRegistryEntity.class);
        entity.setId_colaborador(id_colaborador);
        entity.setNome_colaborador(nome_colaborador);
        entity.setCpf_colaborador(cpf_colaborador);
        return entity;
    }

    public TicketsEntity toTicketsEntity(TicketDTO ticketDTO) {
        return modelMapper.map(ticketDTO, TicketsEntity.class);
    }

    public TicketEntityDTO toTicketEntityDTO(TicketsEntity ticket) {
        return modelMapper.map(ticket, TicketEntityDTO.class);
    }

    public TicketsStripped toTicketsStripped(TicketsEntity ticket) {
        return modelMapper.map(ticket, TicketsStripped.class);
    }

    public WarningsStripped toWarningsStripped(WarningsEntity warning) {
        return modelMapper.map(warning, WarningsStripped.class);
    }
}
