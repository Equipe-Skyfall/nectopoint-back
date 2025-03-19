package com.nectopoint.backend.config;

import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.nectopoint.backend.services.SystemServices;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class StartUpConfig {

    private final SystemServices systemServices;

    @Bean
    public ApplicationRunner runAtStartUp() {
        return _ -> systemServices.createSuperUser();
    }

}
