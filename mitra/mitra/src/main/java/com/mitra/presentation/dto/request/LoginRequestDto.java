package com.mitra.presentation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Payload for user login")
public record LoginRequestDto(
        @NotBlank(message = "Email is required")
        @Email(message = "Email must be valid")
        @Schema(description = "User's email address", example = "test@mitra.com")
        String email,

        @NotBlank(message = "Password is required")
        @Schema(description = "User's password", example = "secret123")
        String password
) {
}
