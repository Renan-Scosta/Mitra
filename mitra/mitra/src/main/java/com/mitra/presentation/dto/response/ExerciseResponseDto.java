package com.mitra.presentation.dto.response;

import com.mitra.domain.model.enums.TrackingType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(description = "Response containing exercise details")
public record ExerciseResponseDto(
        @Schema(description = "Unique identifier of the exercise", example = "1")
        Long id,
        @Schema(description = "Name of the exercise", example = "Bench Press")
        String name,
        @Schema(description = "Target muscle group", example = "Chest")
        String muscleGroup,
        @Schema(description = "Metabolic Equivalent of Task (MET) factor", example = "6.0")
        BigDecimal metFactor,
        @Schema(description = "How this exercise is tracked", example = "WEIGHT_REPS")
        TrackingType trackingType
) {
}
