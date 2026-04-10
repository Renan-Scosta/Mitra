package com.mitra.presentation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Payload to start a new workout session")
public record StartSessionRequestDto(
        @Schema(description = "ID of the user", example = "1")
        Long userId,
        @Schema(description = "ID of the workout routine to be executed", example = "10")
        Long routineId
) {
}
