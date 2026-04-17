package com.mitra.presentation.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(description = "Personal records for a specific exercise")
public record PersonalRecordResponseDto(
        @Schema(description = "Exercise ID", example = "5")
        Long exerciseId,
        @Schema(description = "Exercise name", example = "Bench Press")
        String exerciseName,
        @Schema(description = "Heaviest weight lifted (null for bodyweight/isometric)", example = "105.0")
        BigDecimal maxWeight,
        @Schema(description = "Maximum reps in a single set (null for time-only)", example = "15")
        Integer maxReps,
        @Schema(description = "Max volume = weight x reps (null if not applicable)", example = "1050.0")
        BigDecimal maxVolume,
        @Schema(description = "Longest hold duration in seconds (null for non-isometric)", example = "120")
        Integer maxDuration,
        @Schema(description = "Total sets logged for this exercise", example = "42")
        int totalSets
) {
}
