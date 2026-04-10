package com.mitra.application.usecase;

import com.mitra.presentation.dto.request.CreateExerciseRequestDto;

public interface CreateExerciseUseCase {
    Long execute(CreateExerciseRequestDto request);
}
