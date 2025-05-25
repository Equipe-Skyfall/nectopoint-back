package com.nectopoint.backend.services;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nectopoint.backend.dtos.HolidayDTO;
import com.nectopoint.backend.enums.TipoEscala;
import com.nectopoint.backend.enums.TipoStatusUsuario;
import com.nectopoint.backend.modules.holidays.HolidayEntity;
import com.nectopoint.backend.modules.user.UserSessionEntity;
import com.nectopoint.backend.repositories.holidays.HolidayRepository;
import com.nectopoint.backend.repositories.userSession.UserSessionRepository;

@Service
public class HolidayService {
    
    @Autowired
    private HolidayRepository holidayRepository;
    
    @Autowired
    private UserSessionRepository userSessionRepository;
    
    public HolidayEntity createHoliday(HolidayDTO holidayDTO) {
     
        if (holidayDTO.getEndDate().isBefore(holidayDTO.getStartDate())) {
            throw new IllegalArgumentException("End date cannot be before start date");
        }
        
        HolidayEntity holiday = new HolidayEntity();
        holiday.setName(holidayDTO.getName());
        holiday.setStartDate(holidayDTO.getStartDate());
        holiday.setEndDate(holidayDTO.getEndDate());
        holiday.setDescription(holidayDTO.getDescription());
        holiday.setRepeatsYearly(holidayDTO.getRepeatsYearly());
        holiday.setUserIds(holidayDTO.getUserIds());
        
        return holidayRepository.save(holiday);
    }
    
    public HolidayEntity updateHoliday(String id, HolidayDTO holidayDTO) {
      
        if (holidayDTO.getEndDate().isBefore(holidayDTO.getStartDate())) {
            throw new IllegalArgumentException("End date cannot be before start date");
        }
        
        HolidayEntity holiday = holidayRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Feriado não encontrado"));
            
        holiday.setName(holidayDTO.getName());
        holiday.setStartDate(holidayDTO.getStartDate());
        holiday.setEndDate(holidayDTO.getEndDate());
        holiday.setDescription(holidayDTO.getDescription());
        holiday.setRepeatsYearly(holidayDTO.getRepeatsYearly());
        holiday.setUserIds(holidayDTO.getUserIds());
        
        return holidayRepository.save(holiday);
    }
    
    public void deleteHoliday(String id) {
        HolidayEntity holiday = holidayRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Feriado não encontrado"));
            
        holidayRepository.delete(holiday);
    }
    
    public HolidayEntity getHolidayById(String id) {
        return holidayRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Feriado não encontrado"));
    }
    
    public List<HolidayEntity> getAllHolidays() {
        return holidayRepository.findAll();
    }
    
  
    public boolean isHoliday(Instant date) {
      
        LocalDate localDate = LocalDate.ofInstant(date, ZoneId.systemDefault());
        return isHoliday(localDate);
    }
    
    public boolean isHoliday(LocalDate date, Long userId) {
        List<HolidayEntity> holidays = holidayRepository.findHolidaysForDate(date);
        System.out.println("Checking if " + date + " is a holiday for user " + userId + ". Found " + holidays.size() + " holidays.");
        
      
        List<HolidayEntity> userHolidays = holidays.stream()
            .filter(holiday -> holiday.appliesTo(userId))
            .collect(Collectors.toList());
        
        if (!userHolidays.isEmpty()) {
            for (HolidayEntity holiday : userHolidays) {
                System.out.println("Holiday found for user: " + holiday.getName() + 
                    " (" + holiday.getStartDate() + " to " + holiday.getEndDate() + ")" +
                    (holiday.isGlobalHoliday() ? " [Global]" : " [Personal]"));
            }
        }
        return !userHolidays.isEmpty();
    }
    
    public boolean isHoliday(LocalDate date) {
        List<HolidayEntity> holidays = holidayRepository.findHolidaysForDate(date);
        System.out.println("Checking if " + date + " is a holiday. Found " + holidays.size() + " holidays.");
        
     
        List<HolidayEntity> globalHolidays = holidays.stream()
            .filter(HolidayEntity::isGlobalHoliday)
            .collect(Collectors.toList());
        
        if (!globalHolidays.isEmpty()) {
            for (HolidayEntity holiday : globalHolidays) {
                System.out.println("Global holiday found: " + holiday.getName() + 
                    " (" + holiday.getStartDate() + " to " + holiday.getEndDate() + ")");
            }
        }
        return !globalHolidays.isEmpty();
    }
    
   
    @Transactional
    public void applyHolidayStatusToUsers(Instant date) {
        LocalDate localDate = LocalDate.ofInstant(date, ZoneId.systemDefault());
        applyHolidayStatusToUsers(localDate);
    }
    
 
    @Transactional
    public void applyHolidayStatusToUsers(LocalDate date) {
       
        List<HolidayEntity> allHolidays = holidayRepository.findHolidaysForDate(date);
        
        if (allHolidays.isEmpty()) {
            System.out.println("No holidays found for date: " + date + ". No user status changes needed.");
            return;
        }
        
        
        List<HolidayEntity> globalHolidays = allHolidays.stream()
            .filter(HolidayEntity::isGlobalHoliday)
            .collect(Collectors.toList());
            
        List<HolidayEntity> userSpecificHolidays = allHolidays.stream()
            .filter(holiday -> !holiday.isGlobalHoliday())
            .collect(Collectors.toList());
            
        System.out.println("Found " + globalHolidays.size() + " global holidays and " + 
            userSpecificHolidays.size() + " user-specific holidays for date: " + date);
        
      
        List<UserSessionEntity> allUsers = userSessionRepository.findAll();
        System.out.println("Found " + allUsers.size() + " users to process");
        
        int updatedCount = 0;
        int skippedCount = 0;
        
      
        for (UserSessionEntity user : allUsers) {
            Long userId = user.getId_colaborador();
            TipoStatusUsuario currentStatus = user.getDados_usuario().getStatus();
            
            System.out.println("Processing user ID: " + userId + 
                               ", Name: " + user.getDados_usuario().getNome() + 
                               ", Current Status: " + currentStatus);
           
            if (currentStatus == TipoStatusUsuario.FERIAS || 
                currentStatus == TipoStatusUsuario.INATIVO) {
                System.out.println("Skipping user " + userId + " with status " + currentStatus);
                skippedCount++;
                continue;
            }
            
           
            boolean shouldHaveDayOff = false;
       
            if (!globalHolidays.isEmpty()) {
                shouldHaveDayOff = true;
                System.out.println("User " + userId + " gets day off due to global holiday");
            } else {
     
                for (HolidayEntity holiday : userSpecificHolidays) {
                    if (holiday.getUserIds().contains(userId)) {
                        shouldHaveDayOff = true;
                        System.out.println("User " + userId + " gets day off due to user-specific holiday: " + holiday.getName());
                        break;
                    }
                }
            }
            
      
            if (shouldHaveDayOff) {
                user.getDados_usuario().setStatus(TipoStatusUsuario.FOLGA);
                userSessionRepository.save(user);
                updatedCount++;
                System.out.println("Updated user " + userId + " to FOLGA status");
            }
        }
        
        System.out.println("Holiday status application complete. Updated: " + updatedCount + ", Skipped: " + skippedCount);
    }
    

    @Transactional
    public void processHolidaysForToday() {
    LocalDate today = LocalDate.now();
    System.out.println("Processing holidays and weekend schedules for today: " + today);
    
    // Get holidays for today
    List<HolidayEntity> todayHolidays = holidayRepository.findHolidaysForDate(today);
    
    // Check if there's a global holiday
    boolean hasGlobalHoliday = todayHolidays.stream().anyMatch(HolidayEntity::isGlobalHoliday);
    
    // Get user-specific holidays
    List<HolidayEntity> userSpecificHolidays = todayHolidays.stream()
        .filter(h -> !h.isGlobalHoliday())
        .collect(Collectors.toList());
    
    // Get all user IDs who have a specific holiday today
    List<Long> userIdsWithHoliday = new ArrayList<>();
    for (HolidayEntity holiday : userSpecificHolidays) {
        userIdsWithHoliday.addAll(holiday.getUserIds());
    }
    
    // Get day of week info for weekend processing
    DayOfWeek dayOfWeek = today.getDayOfWeek();
    boolean isSaturday = dayOfWeek == DayOfWeek.SATURDAY;
    boolean isSunday = dayOfWeek == DayOfWeek.SUNDAY;
    boolean isWeekend = isSaturday || isSunday;
    
    System.out.println("Today is " + dayOfWeek + 
                      ", Global holiday: " + hasGlobalHoliday + 
                      ", User-specific holidays: " + userIdsWithHoliday.size());
    
    int holidayCount = 0;
    int weekendCount = 0;
    int skippedCount = 0;
    
    // If there's a global holiday, set all active users to FOLGA
    if (hasGlobalHoliday) {
        List<UserSessionEntity> activeUsers = userSessionRepository.findAll().stream()
            .filter(u -> u.getDados_usuario().getStatus() != TipoStatusUsuario.FERIAS && 
                        u.getDados_usuario().getStatus() != TipoStatusUsuario.INATIVO)
            .collect(Collectors.toList());
        
        System.out.println("Global holiday: Setting " + activeUsers.size() + " active users to FOLGA");
        
        for (UserSessionEntity user : activeUsers) {
            user.getDados_usuario().setStatus(TipoStatusUsuario.FOLGA);
            userSessionRepository.save(user);
            holidayCount++;
        }
    }
    // Otherwise process weekend and user-specific holidays
    else {
        List<UserSessionEntity> allUsers = userSessionRepository.findAll();
        
        for (UserSessionEntity user : allUsers) {
            Long userId = user.getId_colaborador();
            TipoStatusUsuario currentStatus = user.getDados_usuario().getStatus();
            TipoEscala userSchedule = user.getJornada_trabalho().getTipo_escala();
            
            // Skip users on vacation or inactive
            if (currentStatus == TipoStatusUsuario.FERIAS || 
                currentStatus == TipoStatusUsuario.INATIVO) {
                skippedCount++;
                continue;
            }
            
            // Check if user has a specific holiday
            boolean hasSpecificHoliday = userIdsWithHoliday.contains(userId);
            
            // Check for weekend schedule
            boolean isWeekendOff = false;
            if (isWeekend) {
                if (userSchedule == TipoEscala.CINCO_X_DOIS) {
                    // 5x2 users are off on both Saturday and Sunday
                    isWeekendOff = true;
                } else if (userSchedule == TipoEscala.SEIS_X_UM && isSunday) {
                    // 6x1 users are off only on Sunday
                    isWeekendOff = true;
                }
            }
            
            // Set to FOLGA if either condition is met
            if (hasSpecificHoliday || isWeekendOff) {
                user.getDados_usuario().setStatus(TipoStatusUsuario.FOLGA);
                userSessionRepository.save(user);
                
                if (hasSpecificHoliday) {
                    holidayCount++;
                } else {
                    weekendCount++;
                }
            }
        }
    }
    
    System.out.println("Holiday and weekend processing complete. " +
                      "Holiday users: " + holidayCount + 
                      ", Weekend users: " + weekendCount + 
                      ", Skipped: " + skippedCount);
}
    
 
    @Transactional
    public void checkAndPrepareForNextDayHoliday() {
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        System.out.println("Checking if tomorrow (" + tomorrow + ") is a holiday");
        
      
        List<HolidayEntity> tomorrowHolidays = holidayRepository.findHolidaysForDate(tomorrow);
        
        if (!tomorrowHolidays.isEmpty()) {
            System.out.println("Tomorrow has holidays! Preparing user statuses.");
            applyHolidayStatusToUsers(tomorrow);
        } else {
            System.out.println("Tomorrow is not a holiday.");
        }
    }
    
    @Transactional
    public void forceHolidayStatusForUser(Long userId) {
        UserSessionEntity user = userSessionRepository.findByColaborador(userId);
        if (user != null) {
            System.out.println("Force applying holiday status to user ID: " + userId);
            user.getDados_usuario().setStatus(TipoStatusUsuario.FOLGA);
            userSessionRepository.save(user);
            System.out.println("User status updated to FOLGA");
        } else {
            System.out.println("User not found with ID: " + userId);
        }
    }
}