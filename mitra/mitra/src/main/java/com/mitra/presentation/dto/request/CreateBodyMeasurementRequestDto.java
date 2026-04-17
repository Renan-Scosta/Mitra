package com.mitra.presentation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

@Schema(description = "Payload for recording a body measurement")
public record CreateBodyMeasurementRequestDto(
        @NotNull(message = "Weight is required")
        @DecimalMin(value = "20.0", message = "Weight must be at least 20 kg")
        @Schema(description = "Body weight in kg", example = "82.3")
        BigDecimal weightKg,

        @Schema(description = "Body fat percentage (optional)", example = "18.5")
        BigDecimal bodyFatPercentage,

        @NotNull(message = "Record date is required")
        @Schema(description = "Date of the measurement", example = "2026-04-16")
        LocalDate recordDate
) {
}
