package com.ecommerce.security_lib.config;

import com.ecommerce.security_lib.filter.JwtAuthFilter;
import com.ecommerce.security_lib.service.JwtService;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@AutoConfiguration
@EnableWebSecurity
@EnableMethodSecurity
@ComponentScan("com.ecommerce.security_lib")
public class SecurityAutoConfiguration {

    @Bean
    public JwtService jwtService() {
        return new JwtService();
    }

    @Bean
    public JwtAuthFilter jwtAuthFilter(JwtService jwtService) {
        return new JwtAuthFilter(jwtService);
    }
}