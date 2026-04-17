package com.mitra.application.usecase;

import com.mitra.presentation.dto.response.BodyMeasurementResponseDto;

import java.util.List;

public interface GetBodyMeasurementsUseCase {
    List<BodyMeasurementResponseDto> execute(Long userId);
}
