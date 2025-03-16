package com.nectopoint.backend.controllers.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nectopoint.backend.services.PointRegistryService;
import org.springframework.web.bind.annotation.GetMapping;


@RestController
@RequestMapping("/test")
public class TestController {
    
    @Autowired
    private PointRegistryService pointRegistryService;

    @GetMapping("/finalizar-dia")
    public String testEndOfDayProcess() {
        pointRegistryService.endOfDayProcesses();
        return "Testing complete!";
    }
    
}
