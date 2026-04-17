package com.mitra.presentation.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDate;

@Schema(description = "Body measurement record")
public record BodyMeasurementResponseDto(
        Long id,
        BigDecimal weightKg,
        BigDecimal bodyFatPercentage,
        BigDecimal leanMassKg,
        BigDecimal fatMassKg,
        LocalDate recordDate
) {
}
