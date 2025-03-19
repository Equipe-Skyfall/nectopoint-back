package com.nectopoint.backend.dtos;

import com.nectopoint.backend.enums.TipoCargo;

import lombok.Data;

@Data
public class UserDetailsDTO {
    private String name;
    private String cpf;
    private TipoCargo title;
    private String department;
    private String workJourneyType;
    private Long bankOfHours;
    private Integer dailyHours;

    public UserDetailsDTO(String name, String cpf,TipoCargo title, String department, String workJourneyType, Long bankOfHours, Integer dailyHours) {
        this.name = name;
        this.cpf = cpf;
        this.title = title;
        this.department = department;
        this.workJourneyType = workJourneyType;
        this.bankOfHours = bankOfHours;
        this.dailyHours = dailyHours;
    }
}
