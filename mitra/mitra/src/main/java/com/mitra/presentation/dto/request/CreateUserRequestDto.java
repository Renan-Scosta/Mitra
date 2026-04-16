package com.mitra.presentation.dto.request;

import com.mitra.domain.model.enums.Gender;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDate;

@Schema(description = "Payload for registering a new user")
public record CreateUserRequestDto(
        @Schema(description = "Full name", example = "Renan Costa")
        String name,
        @Schema(description = "Valid email address", example = "renan@example.com")
        String email,
        @Schema(description = "Date of birth", example = "1990-05-15")
        LocalDate birthDate,
        @Schema(description = "Biological gender (MALE/FEMALE)", example = "MALE")
        Gender gender,
        @Schema(description = "Height in centimeters", example = "180")
        int heightCm,
        @Schema(description = "Current weight in kg", example = "85.5")
        BigDecimal initialWeightKg,
        @Schema(description = "User's password", example = "secret123")
        String password,
        @Schema(description = "Password confirmation", example = "secret123")
        String confirmPassword
) {
}
