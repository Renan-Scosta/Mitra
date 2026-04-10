package com.mitra.presentation.dto.request;

import com.mitra.domain.model.enums.Gender;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CreateUserRequestDto(
        String name,
        String email,
        LocalDate birthDate,
        Gender gender,
        int heightCm,
        BigDecimal initialWeightKg
) {
}
