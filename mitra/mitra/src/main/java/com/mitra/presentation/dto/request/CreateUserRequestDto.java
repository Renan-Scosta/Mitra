package com.mitra.presentation.dto.request;

import com.mitra.domain.model.enums.Gender;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Schema(description = "Payload for registering a new user")
public record CreateUserRequestDto(
        @NotBlank(message = "Name is required")
        @Schema(description = "Full name", example = "Renan Costa")
        String name,

        @NotBlank(message = "Email is required")
        @Email(message = "Email must be valid")
        @Schema(description = "Valid email address", example = "renan@example.com")
        String email,

        @NotNull(message = "Birth date is required")
        @Past(message = "Birth date must be in the past")
        @Schema(description = "Date of birth", example = "1990-05-15")
        LocalDate birthDate,

        @NotNull(message = "Gender is required")
        @Schema(description = "Biological gender (MALE/FEMALE)", example = "MALE")
        Gender gender,

        @Min(value = 50, message = "Height must be at least 50 cm")
        @Max(value = 300, message = "Height must be at most 300 cm")
        @Schema(description = "Height in centimeters", example = "180")
        int heightCm,

        @NotNull(message = "Initial weight is required")
        @DecimalMin(value = "20.0", message = "Weight must be at least 20 kg")
        @Schema(description = "Current weight in kg", example = "85.5")
        BigDecimal initialWeightKg,

        @NotBlank(message = "Password is required")
        @Size(min = 6, message = "Password must be at least 6 characters")
        @Schema(description = "User's password", example = "secret123")
        String password,

        @NotBlank(message = "Password confirmation is required")
        @Schema(description = "Password confirmation", example = "secret123")
        String confirmPassword
) {
}
