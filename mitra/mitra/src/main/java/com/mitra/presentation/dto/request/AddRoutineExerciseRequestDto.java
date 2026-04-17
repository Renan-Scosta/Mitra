package com.mitra.presentation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Payload for adding an exercise to a workout routine")
public record AddRoutineExerciseRequestDto(
        @NotNull(message = "Exercise ID is required")
        @Schema(description = "ID of the exercise to add", example = "5")
        Long exerciseId,

        @NotNull(message = "Target sets is required")
        @Min(value = 1, message = "Target sets must be at least 1")
        @Schema(description = "Target number of sets", example = "4")
        Integer targetSets,

        @Schema(description = "Target number of repetitions per set", example = "10")
        Integer targetReps
) {
}
