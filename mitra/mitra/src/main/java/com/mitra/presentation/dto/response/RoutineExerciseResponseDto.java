package com.mitra.presentation.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Response containing a routine exercise mapping")
public record RoutineExerciseResponseDto(
        @Schema(description = "Unique identifier of this routine exercise assignment", example = "25")
        Long id,
        @Schema(description = "Exercise details")
        ExerciseResponseDto exercise,
        @Schema(description = "Target number of sets", example = "4")
        Integer targetSets,
        @Schema(description = "Target number of repetitions per set", example = "10")
        Integer targetReps
) {
}
