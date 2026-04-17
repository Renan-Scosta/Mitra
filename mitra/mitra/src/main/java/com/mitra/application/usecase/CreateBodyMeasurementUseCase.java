package com.mitra.application.usecase;

import com.mitra.presentation.dto.request.CreateBodyMeasurementRequestDto;

public interface CreateBodyMeasurementUseCase {
    Long execute(Long userId, CreateBodyMeasurementRequestDto request);
}
