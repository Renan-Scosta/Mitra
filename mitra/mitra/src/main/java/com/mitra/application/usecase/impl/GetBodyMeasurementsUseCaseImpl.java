package com.mitra.application.usecase.impl;

import com.mitra.application.port.out.BodyMeasurementRepositoryPort;
import com.mitra.application.usecase.GetBodyMeasurementsUseCase;
import com.mitra.domain.model.BodyMeasurement;
import com.mitra.presentation.dto.response.BodyMeasurementResponseDto;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GetBodyMeasurementsUseCaseImpl implements GetBodyMeasurementsUseCase {

    private final BodyMeasurementRepositoryPort bodyMeasurementRepositoryPort;

    public GetBodyMeasurementsUseCaseImpl(BodyMeasurementRepositoryPort bodyMeasurementRepositoryPort) {
        this.bodyMeasurementRepositoryPort = bodyMeasurementRepositoryPort;
    }

    @Override
    public List<BodyMeasurementResponseDto> execute(Long userId) {
        return bodyMeasurementRepositoryPort.findAllByUserId(userId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    private BodyMeasurementResponseDto toDto(BodyMeasurement bm) {
        return new BodyMeasurementResponseDto(
                bm.getId(),
                bm.getWeightKg(),
                bm.getBodyFatPercentage(),
                bm.getLeanMass().orElse(null),
                bm.getFatMass().orElse(null),
                bm.getRecordDate()
        );
    }
}
