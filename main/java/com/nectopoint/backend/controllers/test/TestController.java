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

    @GetMapping("/finalizar-dia")
    public String testEndOfDayProcess() {
        systemServices.endOfDayProcesses();
        return "Testing complete!";
    }
    
    @GetMapping("/sync-databases")
    public String syncDatabases() {
        userSessionService.syncUsersWithSessions();
        return "Databases synced!";
    }
    
}
