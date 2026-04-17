package com.mitra.application.usecase;

import com.mitra.presentation.dto.response.WorkoutSessionResponseDto;

import java.util.List;

public interface GetUserSessionsUseCase {
    List<WorkoutSessionResponseDto> execute(Long userId);
}
