package com.nectopoint.backend.config;

import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.nectopoint.backend.dtos.UserDetailsDTO;
import com.nectopoint.backend.dtos.UserSessionDTO;
import com.nectopoint.backend.modules.user.UserSessionEntity;

@Configuration
public class ModelMapperConfig {
    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();

        modelMapper.addMappings(new PropertyMap<UserDetailsDTO, UserSessionEntity>() {
            @Override
            protected void configure() {
                map().setId_colaborador(source.getId());

                map().getDados_usuario().setNome(source.getName());
                map().getDados_usuario().setCpf(source.getCpf());
                map().getDados_usuario().setCargo(source.getTitle());
                map().getDados_usuario().setDepartamento(source.getDepartment());

                map().getJornada_trabalho().setTipo_jornada(source.getWorkJourneyType());
                map().getJornada_trabalho().setBanco_de_horas(source.getBankOfHours());
                map().getJornada_trabalho().setHoras_diarias(source.getDailyHours());
            }      
        });

        modelMapper.addMappings(new PropertyMap<UserSessionEntity, UserSessionDTO>() {
            @Override
            protected void configure() {
                map().setId_colaborador(source.getId_colaborador());
                map().setNome(source.getDados_usuario().getNome());
                map().setCpf(source.getDados_usuario().getCpf());
                map().setCargo(source.getDados_usuario().getCargo());
                map().setDepartamento(source.getDados_usuario().getDepartamento());
                map().setStatus(source.getDados_usuario().getStatus());
                map().setBanco_de_horas(source.getJornada_trabalho().getBanco_de_horas());
                map().setHoras_diarias(source.getJornada_trabalho().getHoras_diarias());
            }
        });

        return modelMapper;
    }
}
