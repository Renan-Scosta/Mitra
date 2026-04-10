package com.mitra.application.usecase;

import com.mitra.presentation.dto.response.WorkoutSessionResponseDto;

public interface GetWorkoutSessionUseCase {
    WorkoutSessionResponseDto execute(Long sessionId);
}
