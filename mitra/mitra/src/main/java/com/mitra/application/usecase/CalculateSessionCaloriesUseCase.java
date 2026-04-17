package com.mitra.application.usecase;

import com.mitra.presentation.dto.response.SessionCaloriesResponseDto;

public interface CalculateSessionCaloriesUseCase {
    SessionCaloriesResponseDto execute(Long userId, Long sessionId);
}
