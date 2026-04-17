package com.mitra.presentation.dto.request;

import com.mitra.domain.model.enums.TrackingType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

@Schema(description = "Payload for registering a new exercise")
public record CreateExerciseRequestDto(
        @NotBlank(message = "Exercise name is required")
        @Schema(description = "Name of the exercise", example = "Bench Press")
        String name,

        @NotBlank(message = "Muscle group is required")
        @Schema(description = "Target muscle group", example = "Chest")
        String muscleGroup,

        @NotNull(message = "MET factor is required")
        @DecimalMin(value = "0.1", message = "MET factor must be positive")
        @Schema(description = "Metabolic Equivalent of Task (MET) factor", example = "6.0")
        BigDecimal metFactor,

        @NotNull(message = "Tracking type is required")
        @Schema(description = "How this exercise is tracked (WEIGHT_REPS, REPS_ONLY, TIME_ONLY)", example = "WEIGHT_REPS")
        TrackingType trackingType
) {
}
