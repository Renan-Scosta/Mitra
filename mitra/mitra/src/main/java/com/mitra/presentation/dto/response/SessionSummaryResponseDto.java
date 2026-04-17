package com.mitra.presentation.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Summary of a finished workout session")
public record SessionSummaryResponseDto(
        @Schema(description = "Session ID", example = "100")
        Long sessionId,
        @Schema(description = "Total number of sets completed", example = "15")
        Integer totalSets,
        @Schema(description = "Effective duration of the workout in minutes", example = "45")
        Long effectiveDurationMinutes,
        @Schema(description = "Estimated calories burned (null if user weight is not recorded)", example = "245.5")
        Double estimatedCalories
) {
}
