package com.nectopoint.backend.services;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.nectopoint.backend.enums.TipoCargo;
import com.nectopoint.backend.enums.TipoEscala;
import com.nectopoint.backend.modules.holidays.HolidayEntity;
import com.nectopoint.backend.modules.user.UserEntity;
import com.nectopoint.backend.repositories.UserRepository;
import com.nectopoint.backend.repositories.holidays.HolidayRepository;

@Service
public class SystemServices {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private HolidayRepository holidayRepository;

    @Autowired
    @Lazy
    private UserService userService;
    @Autowired
    @Lazy
    private UserSessionService userSessionService;
    @Autowired
    @Lazy
    private PointRegistryService registryService;
    @Autowired
    @Lazy
    private WarningsService warningsService;
    @Autowired
    @Lazy
    private TicketsService ticketsService;
    @Autowired
    @Lazy
    private HolidayService holidayService;

   @Scheduled(cron = "0 0 0 * * ?", zone = "America/Sao_Paulo")
public void newDayProcesses() {
    // LocalDate today = LocalDate.now();
    // System.out.println("Starting midnight processes for: " + today);
    
    // // First handle day of week/weekend logic
    // DayOfWeek dayOfWeek = today.getDayOfWeek();
    // System.out.println("Today is: " + dayOfWeek);
    
    // TipoEscala escala;

    // if (dayOfWeek == DayOfWeek.SATURDAY) {
    //     System.out.println("Today is Saturday - Setting 5x2 users to FOLGA");
    //     escala = TipoEscala.CINCO_X_DOIS;
    //     userSessionService.startWeekend(escala);
    // } else if (dayOfWeek == DayOfWeek.SUNDAY) {
    //     System.out.println("Today is Sunday - Setting 6x1 users to FOLGA");
    //     escala = TipoEscala.SEIS_X_UM;
    //     userSessionService.startWeekend(escala);
    // } else if (dayOfWeek == DayOfWeek.MONDAY) {
    //     System.out.println("Today is Monday - Regular day processing");
    // }
    
 
    // List<HolidayEntity> todayHolidays = holidayRepository.findHolidaysForDate(today);
    // if (!todayHolidays.isEmpty()) {
    //     System.out.println("Today has " + todayHolidays.size() + " holidays - applying holiday status to users");
    //     holidayService.processHolidaysForToday();
    // }
    
    
    // Instant now = today.atStartOfDay(ZoneId.systemDefault()).toInstant();
    // userSessionService.startVacation(now);
    
    // System.out.println("Midnight processes completed");

      LocalDate today = LocalDate.now();
    System.out.println("Starting midnight processes for: " + today);
    
    // Use holiday service to handle both holidays AND weekends
    holidayService.processHolidaysForToday();
    
    // Handle vacation starts/ends
    Instant now = today.atStartOfDay(ZoneId.systemDefault()).toInstant();
    userSessionService.startVacation(now);
    
    System.out.println("Midnight processes completed");
}

    @Scheduled(cron = "0 0 22 * * ?", zone = "America/Sao_Paulo")
    public void endOfDayProcesses() {
        System.out.println("Starting end of day processes (10 PM)");
        registryService.endDayShifts();
        
        // // verifica feriados AMANHÃ!!!!
        // LocalDate tomorrow = LocalDate.now().plusDays(1);
        // List<HolidayEntity> tomorrowHolidays = holidayRepository.findHolidaysForDate(tomorrow);
        // // MUDA O STATUS PARA FOLGA SE TIVER FERIADO AMANHÃ!!!!
        // if (!tomorrowHolidays.isEmpty()) {
        //     System.out.println("Tomorrow has " + tomorrowHolidays.size() + " holidays - preparing user statuses");
        //     holidayService.applyHolidayStatusToUsers(tomorrow);
        // } else {
        //     System.out.println("Tomorrow has no holidays");
        // }
        
        System.out.println("End of day processes completed");
    }

@Scheduled(cron = "0 0 6 * * ?", zone = "America/Sao_Paulo")
public void startOfDayProcesses() {
    LocalDate today = LocalDate.now();
    System.out.println("Starting morning processes (6 AM) for: " + today);
    
    List<HolidayEntity> todayHolidays = holidayRepository.findHolidaysForDate(today);
    
    if (todayHolidays.isEmpty()) {
        System.out.println("Today has no holidays - setting users to ESCALADO");
        registryService.startDayShifts();
    } else {
        boolean hasGlobalHoliday = todayHolidays.stream().anyMatch(HolidayEntity::isGlobalHoliday);
        
        if (hasGlobalHoliday) {
            //SETADO NO PROCESSO DAS DEZ
            System.out.println("Today has global holidays - all users will have FOLGA status");
           
        } else {
            System.out.println("Today has only user-specific holidays - only specified users will have FOLGA status");
            
        
            for (HolidayEntity holiday : todayHolidays) {
                System.out.println("Holiday: " + holiday.getName() + ", applies to users: " + holiday.getUserIds());
            }
            
        
            registryService.startDayShifts();
        }
    }
    System.out.println("Morning processes completed");
}

    public void clearUserData(Long id) {
        registryService.deleteAllByColaborador(id);
        ticketsService.deleteAllByColaborador(id);
        warningsService.deleteAllByColaborador(id);
    }

    public void createSuperUser() {
        Optional<UserEntity> superUserOpt = userRepository.findByEmployeeNumber("SUD000");

        if (superUserOpt.isEmpty()) {
            UserEntity superUser = new UserEntity();
            superUser.setName("Super User");
            superUser.setEmail("nectopointfatec@gmail.com");
            superUser.setPassword("Necto-123");
            superUser.setCpf("00000000000");
            superUser.setTitle(TipoCargo.GERENTE);
            superUser.setDepartment("Super department");
            superUser.setWorkJourneyType(TipoEscala.CINCO_X_DOIS);
            superUser.setEmployeeNumber("SUD000");
            superUser.setDailyHours(25);
            superUser.setBirthDate(java.time.LocalDate.of(2000, 1, 1));

            userService.createUser(superUser);
        }
    }
}