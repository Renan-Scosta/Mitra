package com.mitra.presentation.dto.request;

import com.mitra.domain.model.enums.Gender;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;

public record UpdateUserProfileRequestDto(
        @NotBlank(message = "Name is required")
        String name,

        @NotNull(message = "Birth date is required")
        @Past(message = "Birth date must be in the past")
        LocalDate birthDate,

        @NotNull(message = "Gender is required")
        Gender gender,

        @NotNull(message = "Height is required")
        @Positive(message = "Height must be positive")
        Integer heightCm
) {}
