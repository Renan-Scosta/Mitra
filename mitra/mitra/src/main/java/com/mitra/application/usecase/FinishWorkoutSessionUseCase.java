package com.mitra.application.usecase;

import com.mitra.presentation.dto.response.SessionSummaryResponseDto;

public interface FinishWorkoutSessionUseCase {
    SessionSummaryResponseDto execute(Long sessionId);
}
