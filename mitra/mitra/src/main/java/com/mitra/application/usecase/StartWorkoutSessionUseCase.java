package com.mitra.application.usecase;

import com.mitra.presentation.dto.request.StartSessionRequestDto;

public interface StartWorkoutSessionUseCase {
    Long execute(StartSessionRequestDto request);
}
