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
}
