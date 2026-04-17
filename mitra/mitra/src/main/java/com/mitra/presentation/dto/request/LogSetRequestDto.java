package com.mitra.presentation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

@Schema(description = "Payload to log a set execution for an exercise")
public record LogSetRequestDto(
        @NotNull(message = "Exercise ID is required")
        @Schema(description = "ID of the exercise", example = "5")
        Long exerciseId,

        @Schema(description = "Weight lifted in kg (if applicable)", example = "45.0")
        BigDecimal weightKg,

        @Schema(description = "Number of repetitions performed", example = "12")
        Integer reps,

        @Schema(description = "Duration in seconds (if applicable)", example = "60")
        Integer durationSeconds
) {
}
