package com.nectopoint.backend.services;

import org.springframework.beans.factory.annotation.Autowired;

public class SystemServices {
    
    @Autowired
    private PointRegistryService registryService;
    @Autowired
    private TicketsService ticketsService;
    @Autowired
    private WarningsService warningsService;

    public void clearUserData(Long id) {
        registryService.deleteAllByColaborador(id);
        ticketsService.deleteAllByColaborador(id);
        warningsService.deleteAllByColaborador(id);
    }

}
