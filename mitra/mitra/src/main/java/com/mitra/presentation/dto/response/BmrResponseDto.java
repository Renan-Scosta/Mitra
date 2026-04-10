package com.mitra.presentation.dto.response;

import java.time.LocalDateTime;

public record BmrResponseDto(
        double bmr,
        LocalDateTime calculatedAt
) {
}
