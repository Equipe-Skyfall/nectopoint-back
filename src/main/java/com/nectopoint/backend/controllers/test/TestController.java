package com.nectopoint.backend.controllers.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nectopoint.backend.services.SystemServices;
import com.nectopoint.backend.services.UserSessionService;

import org.springframework.web.bind.annotation.GetMapping;



@RestController
@RequestMapping("/test")
public class TestController {
    
    @Autowired
    private UserSessionService userSessionService;
    @Autowired
    private SystemServices systemServices;

    @GetMapping("/processos-das-dez")
    public String testEndOfDayProcess() {
        systemServices.endOfDayProcesses();
        return "Dia finalizado!";
    }

    @GetMapping("/processos-das-seis")
    public String testStartOfDayProcess() {
        systemServices.startOfDayProcesses();
        return "Dia iniciado!";
    }

    @GetMapping("/processos-da-meianoite")
    public String testNewDayProcesses() {
        systemServices.newDayProcesses();
        return "Virada do dia concluída!";
    }
    
    @GetMapping("/sync-databases")
    public String syncDatabases() {
        userSessionService.syncUsersWithSessions();
        return "Databases synced!";
    }
    
}
