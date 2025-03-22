package com.nectopoint.backend.services;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.nectopoint.backend.enums.TipoCargo;
import com.nectopoint.backend.modules.user.UserEntity;
import com.nectopoint.backend.repositories.UserRepository;

@Service
public class SystemServices {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    @Lazy
    private UserService userService;
    @Autowired
    @Lazy
    private PointRegistryService registryService;
    @Autowired
    @Lazy
    private WarningsService warningsService;
    @Autowired
    @Lazy
    private TicketsService ticketsService;

    @Scheduled(cron = "0 0 22 * * ?", zone = "America/Sao_Paulo")
    public void endOfDayProcesses() {
        registryService.endDayShifts();
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
            superUser.setEmail("necto@necto.com");
            superUser.setPassword("Necto-123");
            superUser.setCpf("00000000000");
            superUser.setTitle(TipoCargo.GERENTE);
            superUser.setDepartment("Super department");
            superUser.setWorkJourneyType("When the world most needs him.");
            superUser.setEmployeeNumber("SUD000");
            superUser.setDailyHours(25);
            superUser.setBirthDate(LocalDate.of(2000, 1, 1));

            userService.createUser(superUser);
        }
    }
}
