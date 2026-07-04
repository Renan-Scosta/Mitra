package com.mitra.application.usecase;

import com.mitra.presentation.dto.response.BodyMeasurementResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface GetBodyMeasurementsUseCase {
    Page<BodyMeasurementResponseDto> execute(Long userId, Pageable pageable);
}
