package com.mitra.application.usecase;

import com.mitra.presentation.dto.request.CreateRoutineRequestDto;

public interface CreateWorkoutRoutineUseCase {
    Long execute(Long userId, CreateRoutineRequestDto request);
}
