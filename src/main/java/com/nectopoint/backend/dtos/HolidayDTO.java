package com.nectopoint.backend.dtos;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class HolidayDTO {
    private String id;
    
    @NotBlank(message = "Nome do feriado é obrigatório")
    private String name;
    
    @NotNull(message = "Data de início do feriado é obrigatória")
    private LocalDate startDate; 
    
    @NotNull(message = "Data de término do feriado é obrigatória")
    private LocalDate endDate; 
    
    private String description;
    
    private Boolean repeatsYearly = false;
    

    private List<Long> userIds = new ArrayList<>();
}