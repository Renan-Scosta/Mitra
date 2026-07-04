package com.mitra.application.usecase.impl;

import com.mitra.application.port.out.BodyMeasurementRepositoryPort;
import com.mitra.application.usecase.GetBodyMeasurementsUseCase;
import com.mitra.domain.model.BodyMeasurement;
import com.mitra.presentation.dto.response.BodyMeasurementResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class GetBodyMeasurementsUseCaseImpl implements GetBodyMeasurementsUseCase {

    private final BodyMeasurementRepositoryPort bodyMeasurementRepositoryPort;

    public GetBodyMeasurementsUseCaseImpl(
            BodyMeasurementRepositoryPort bodyMeasurementRepositoryPort) {
        this.bodyMeasurementRepositoryPort = bodyMeasurementRepositoryPort;
    }

    @Override
    public Page<BodyMeasurementResponseDto> execute(Long userId, Pageable pageable) {
        log.debug("Listing measurements for userId={}", userId);
        return bodyMeasurementRepositoryPort.findAllByUserId(userId, pageable).map(this::toDto);
    }

    private BodyMeasurementResponseDto toDto(BodyMeasurement bm) {
        return new BodyMeasurementResponseDto(
                bm.getId(),
                bm.getWeightKg(),
                bm.getBodyFatPercentage(),
                bm.getLeanMass().orElse(null),
                bm.getFatMass().orElse(null),
                bm.getRecordDate());
    }
}
