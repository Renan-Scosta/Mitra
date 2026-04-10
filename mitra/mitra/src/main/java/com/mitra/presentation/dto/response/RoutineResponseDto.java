package com.mitra.presentation.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Response containing workout routine details")
public record RoutineResponseDto(
        @Schema(description = "Unique identifier of the routine", example = "10")
        Long id,
        @Schema(description = "ID of the user who owns the routine", example = "1")
        Long userId,
        @Schema(description = "Name of the routine", example = "Full Body Workout A")
        String name,
        @Schema(description = "List of exercises belonging to this routine")
        List<RoutineExerciseResponseDto> exercises
) {
}
