package com.nectopoint.backend.dtos;

import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;

import com.nectopoint.backend.enums.TipoCargo;
import com.nectopoint.backend.modules.user.UserSessionEntity;

import lombok.Data;

@Data
public class UserDetailsDTO {
    private Long id;
    private String name;
    private String cpf;
    private TipoCargo title;
    private String department;
    private String workJourneyType;
    private Long bankOfHours;
    private Integer dailyHours;

    public UserSessionEntity toUserSessionEntity() {
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

        return modelMapper.map(this, UserSessionEntity.class);
    }
}
