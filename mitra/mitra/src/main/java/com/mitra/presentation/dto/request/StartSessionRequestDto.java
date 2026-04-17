package com.mitra.presentation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Payload to start a new workout session")
public record StartSessionRequestDto(
        @NotNull(message = "Routine ID is required")
        @Schema(description = "ID of the workout routine to be executed", example = "10")
        Long routineId
) {
}
