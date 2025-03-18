package com.nectopoint.backend.security;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
// import com.nectopoint.backend.services.AuthorizationService;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
public class SecurityConfig {


    @Autowired
    private SecurityUserFilter securityUserFilter;

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{

        http.csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth ->{
            auth.requestMatchers("/usuario/auth").permitAll();
                auth.anyRequest().authenticated();
            })
            .addFilterBefore(securityUserFilter,UsernamePasswordAuthenticationFilter.class);
            
            ;
        return http.build();
    }


    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

}
