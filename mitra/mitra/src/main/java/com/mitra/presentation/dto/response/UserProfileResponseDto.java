package com.mitra.presentation.dto.response;

import com.mitra.domain.model.enums.Gender;
import java.time.LocalDate;

public record UserProfileResponseDto(
        Long id,
        String email,
        String name,
        LocalDate birthDate,
        Gender gender,
        int heightCm,
        int age
) {}
