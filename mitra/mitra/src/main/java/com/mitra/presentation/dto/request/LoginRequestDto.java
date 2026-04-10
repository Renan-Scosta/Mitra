package com.mitra.presentation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Payload for user login")
public record LoginRequestDto(
        @Schema(description = "User's email address", example = "test@mitra.com")
        String email
) {
}
