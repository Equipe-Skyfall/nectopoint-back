package com.nectopoint.backend.dtos;

import com.nectopoint.backend.enums.TipoCargo;

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
}
