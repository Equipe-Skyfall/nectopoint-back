package com.nectopoint.backend.security;
import org.springframework.context.annotation.Bean;
// import com.nectopoint.backend.services.AuthorizationService;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{

        http.csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth ->{
                auth.requestMatchers("/usuario/").permitAll(); //Lembrar de alterar para login apenas
                auth.anyRequest().authenticated();
            })
        ;
        return http.build();
    }


    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

}
