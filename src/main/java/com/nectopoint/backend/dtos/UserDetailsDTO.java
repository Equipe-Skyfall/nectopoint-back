package com.nectopoint.backend.dtos;

import com.nectopoint.backend.enums.TipoCargo;

import lombok.Data;

@Data
public class UserDetailsDTO {
    private String nome;
    private String cpf;
    private TipoCargo title;
    private String department;
    private String workJourneyType;
    private Float bankOfHours;
    private Integer dailyHours;

    public UserDetailsDTO(String nome, String cpf,TipoCargo title, String department, String workJourneyType, Float bankOfHours, Integer dailyHours) {
        this.nome = nome;
        this.cpf = cpf;
        this.title = title;
        this.department = department;
        this.workJourneyType = workJourneyType;
        this.bankOfHours = bankOfHours;
        this.dailyHours = dailyHours;
    }
}
