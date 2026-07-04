package com.mitra.infrastructure.config;

import com.mitra.domain.service.BmrCalculator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UseCaseConfig {

    @Bean
    public BmrCalculator bmrCalculator() {
        return new BmrCalculator();
    }
}
