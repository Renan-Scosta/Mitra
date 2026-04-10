package com.mitra.presentation.dto.request;

import com.mitra.domain.model.enums.TrackingType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(description = "Payload for registering a new exercise")
public record CreateExerciseRequestDto(
        @Schema(description = "Name of the exercise", example = "Bench Press")
        String name,
        @Schema(description = "Target muscle group", example = "Chest")
        String muscleGroup,
        @Schema(description = "Metabolic Equivalent of Task (MET) factor", example = "6.0")
        BigDecimal metFactor,
        @Schema(description = "How this exercise is tracked (WEIGHT_REPS, DISTANCE_TIME, TIME_ONLY)", example = "WEIGHT_REPS")
        TrackingType trackingType
) {
}
