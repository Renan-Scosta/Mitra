package com.mitra.presentation.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Response containing the Basal Metabolic Rate (BMR) calculation")
public record BmrResponseDto(
        @Schema(description = "Calculated Basal Metabolic Rate value in kcal/day", example = "1850.5")
        double bmr,
        @Schema(description = "Date and time when the calculation was performed", example = "2026-04-10T16:20:00")
        LocalDateTime calculatedAt
) {
}
