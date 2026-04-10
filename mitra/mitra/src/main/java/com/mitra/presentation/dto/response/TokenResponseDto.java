package com.mitra.presentation.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Response containing the JWT token")
public record TokenResponseDto(
        @Schema(description = "JWT token string", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
        String token
) {
}
