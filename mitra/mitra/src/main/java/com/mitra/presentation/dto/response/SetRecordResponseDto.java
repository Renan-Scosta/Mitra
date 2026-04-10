package com.mitra.presentation.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;

@Schema(description = "Set record details")
public record SetRecordResponseDto(
        @Schema(description = "Set ID", example = "30")
        Long id,
        @Schema(description = "Exercise ID", example = "5")
        Long exerciseId,
        @Schema(description = "Weight lifted in kg", example = "45.0")
        BigDecimal weightKg,
        @Schema(description = "Number of repetitions", example = "12")
        Integer reps,
        @Schema(description = "Duration in seconds", example = "60")
        Integer durationSeconds
) {
}
