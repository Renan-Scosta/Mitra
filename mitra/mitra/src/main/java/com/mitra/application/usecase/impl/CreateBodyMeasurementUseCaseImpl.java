package com.mitra.application.usecase.impl;

import com.mitra.application.port.out.BodyMeasurementRepositoryPort;
import com.mitra.application.usecase.CreateBodyMeasurementUseCase;
import com.mitra.domain.model.BodyMeasurement;
import com.mitra.presentation.dto.request.CreateBodyMeasurementRequestDto;
import org.springframework.stereotype.Service;

@Service
public class CreateBodyMeasurementUseCaseImpl implements CreateBodyMeasurementUseCase {

    private final BodyMeasurementRepositoryPort bodyMeasurementRepositoryPort;

    public CreateBodyMeasurementUseCaseImpl(BodyMeasurementRepositoryPort bodyMeasurementRepositoryPort) {
        this.bodyMeasurementRepositoryPort = bodyMeasurementRepositoryPort;
    }

    @Override
    public Long execute(Long userId, CreateBodyMeasurementRequestDto request) {
        BodyMeasurement measurement = BodyMeasurement.builder()
                .userId(userId)
                .weightKg(request.weightKg())
                .bodyFatPercentage(request.bodyFatPercentage())
                .recordDate(request.recordDate())
                .build();

        BodyMeasurement saved = bodyMeasurementRepositoryPort.save(measurement);
        return saved.getId();
    }
}
