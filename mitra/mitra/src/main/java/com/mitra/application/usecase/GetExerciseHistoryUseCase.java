package com.mitra.application.usecase;

import com.mitra.presentation.dto.response.ExerciseHistoryResponseDto;

public interface GetExerciseHistoryUseCase {
    ExerciseHistoryResponseDto execute(Long userId, Long exerciseId);
}
