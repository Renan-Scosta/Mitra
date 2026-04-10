package com.mitra.presentation.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "Workout session details including logged sets")
public record WorkoutSessionResponseDto(
        @Schema(description = "Session ID", example = "100")
        Long id,
        @Schema(description = "User ID", example = "1")
        Long userId,
        @Schema(description = "Routine ID", example = "10")
        Long routineId,
        @Schema(description = "Time the session started", example = "2026-04-10T16:20:00")
        LocalDateTime startTime,
        @Schema(description = "Time the session ended (if finished)", example = "2026-04-10T17:20:00")
        LocalDateTime endTime,
        @Schema(description = "Indicates whether the session is currently active")
        boolean active,
        @Schema(description = "Logged sets grouped into the session")
        List<SetRecordResponseDto> setRecords
) {
}
