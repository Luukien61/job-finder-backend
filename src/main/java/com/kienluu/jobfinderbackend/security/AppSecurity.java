package com.kienluu.jobfinderbackend.security;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfigurationSource;

@EnableWebSecurity
@EnableMethodSecurity
@Configuration
@AllArgsConstructor
public class AppSecurity {
    private CorsConfigurationSource configurationSource;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(corsConfigurer -> corsConfigurer.configurationSource(configurationSource))
                .csrf(AbstractHttpConfigurer::disable) // (1)
                .formLogin(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests((request) -> request
                        .requestMatchers(HttpMethod.POST,"/chat/all").permitAll()
                        .requestMatchers("/**").permitAll()
                )
        ;
        return http.build();
    }
}
