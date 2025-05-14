package com.nectopoint.backend.modules.holidays;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Document(collection = "feriados")
@Data
public class HolidayEntity {
    @Id
    private String id;
    
    @Indexed
    private String name;
    
    @Indexed
    private LocalDate startDate; 
    
    @Indexed
    private LocalDate endDate; 
    
    private String description;
    
    private Boolean repeatsYearly;
    
    // Lista de ids de usuários que a folga se aplica
    // se vazio ou nulo se aplica a todos
    private List<Long> userIds = new ArrayList<>();
    

    public boolean appliesTo(Long userId) {
      
        if (userIds == null || userIds.isEmpty()) {
            return true;
        }
        
      
        return userIds.contains(userId);
    }
 
    public boolean isGlobalHoliday() {
        return userIds == null || userIds.isEmpty();
    }
}