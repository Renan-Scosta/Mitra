package com.mitra.presentation.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "Training history for a specific exercise grouped by session")
public record ExerciseHistoryResponseDto(
        @Schema(description = "Exercise ID", example = "5")
        Long exerciseId,
        @Schema(description = "Exercise name", example = "Deadlift")
        String exerciseName,
        @Schema(description = "List of sessions where this exercise was performed, ordered chronologically (newest first)")
        List<SessionHistoryDto> sessions
) {
    @Schema(description = "A single session containing sets of the exercise")
    public record SessionHistoryDto(
            @Schema(description = "Session ID", example = "100")
            Long sessionId,
            @Schema(description = "Date and time of the session", example = "2026-04-10T16:20:00")
            LocalDateTime date,
            @Schema(description = "Sets performed in this session")
            List<SetHistoryDto> sets
    ) {}

    @Schema(description = "A single set execution")
    public record SetHistoryDto(
            @Schema(description = "Weight lifted in kg (if applicable)", example = "140.0")
            BigDecimal weightKg,
            @Schema(description = "Number of repetitions (if applicable)", example = "8")
            Integer reps,
            @Schema(description = "Duration in seconds (if applicable)", example = "60")
            Integer durationSeconds
    ) {}
}
