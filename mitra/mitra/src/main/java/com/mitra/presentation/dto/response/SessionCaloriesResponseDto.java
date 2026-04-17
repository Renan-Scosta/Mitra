package com.mitra.presentation.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "Response containing estimated calorie expenditure for a workout session")
public record SessionCaloriesResponseDto(
        @Schema(description = "Session ID", example = "100")
        Long sessionId,
        @Schema(description = "Total estimated calories burned", example = "245.5")
        double totalCalories,
        @Schema(description = "Breakdown of calories burned per exercise")
        List<ExerciseCaloriesDto> perExercise
) {
    @Schema(description = "Calories burned for a specific exercise")
    public record ExerciseCaloriesDto(
            @Schema(description = "Exercise name", example = "Deadlift")
            String exerciseName,
            @Schema(description = "Calories burned", example = "120.5")
            double calories
    ) {}
}
