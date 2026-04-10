package com.mitra.presentation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Payload for adding an exercise to a workout routine")
public record AddRoutineExerciseRequestDto(
        @Schema(description = "ID of the exercise to add", example = "5")
        Long exerciseId,
        @Schema(description = "Target number of sets", example = "4")
        Integer targetSets,
        @Schema(description = "Target number of repetitions per set", example = "10")
        Integer targetReps
) {
}
