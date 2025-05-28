package com.nectopoint.backend.utils;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;

import org.springframework.stereotype.Component;

@Component
public class DateTimeHelper {
    public Instant joinDateTime(Instant dia, Instant hora) {
        ZoneId zone = ZoneId.of("America/Sao_Paulo");
    
        // Extract the date part from 'dia' and the time part from 'hora'
        LocalDate datePart = LocalDateTime.ofInstant(dia, ZoneOffset.UTC).atZone(zone).toLocalDate();
        LocalTime timePart = hora.atZone(zone).toLocalTime();
        
        // Combine them into a LocalDateTime
        LocalDateTime combined = LocalDateTime.of(datePart, timePart);
        
        // Convert back to an Instant
        return combined.atZone(zone).toInstant();
    }
}
