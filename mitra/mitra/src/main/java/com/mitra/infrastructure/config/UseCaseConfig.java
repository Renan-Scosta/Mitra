package com.mitra.infrastructure.config;

import com.mitra.application.port.out.BodyMeasurementRepositoryPort;
import com.mitra.application.port.out.UserRepositoryPort;
import com.mitra.application.usecase.CalculateBmrUseCase;
import com.mitra.domain.service.BmrCalculator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UseCaseConfig {

    @Bean
    public BmrCalculator bmrCalculator() {
        return new BmrCalculator();
    }

    @Bean
    public CalculateBmrUseCase calculateBmrUseCase(
            UserRepositoryPort userRepositoryPort,
            BodyMeasurementRepositoryPort bodyMeasurementRepositoryPort,
            BmrCalculator bmrCalculator) {
        return new CalculateBmrUseCase(userRepositoryPort, bodyMeasurementRepositoryPort, bmrCalculator);
    }

    @Bean
    public com.mitra.application.usecase.RegisterUserUseCase registerUserUseCase(
            UserRepositoryPort userRepositoryPort,
            BodyMeasurementRepositoryPort bodyMeasurementRepositoryPort) {
        return new com.mitra.application.usecase.impl.RegisterUserUseCaseImpl(userRepositoryPort, bodyMeasurementRepositoryPort);
    }
}
