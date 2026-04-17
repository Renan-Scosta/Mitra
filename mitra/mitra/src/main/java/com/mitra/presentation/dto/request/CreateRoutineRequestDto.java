package com.mitra.presentation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Payload for creating a new workout routine")
public record CreateRoutineRequestDto(
        @NotBlank(message = "Routine name is required")
        @Schema(description = "Name of the routine", example = "Full Body Workout A")
        String name
) {
}
