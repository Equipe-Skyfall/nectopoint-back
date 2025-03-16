package com.nectopoint.backend.dtos;

import com.nectopoint.backend.enums.TipoCargo;

import lombok.Data;

@Data
public class UserDetailsDTO {
    private TipoCargo title;
    private String department;
    private String workJourneyType;
    private Float bankOfHours;
    private Integer dailyHours;

    public UserDetailsDTO(TipoCargo title, String department, String workJourneyType, Float bankOfHours, Integer dailyHours) {
        this.title = title;
        this.department = department;
        this.workJourneyType = workJourneyType;
        this.bankOfHours = bankOfHours;
        this.dailyHours = dailyHours;
    }
}
