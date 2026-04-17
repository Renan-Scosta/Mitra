package com.mitra.application.usecase;

import com.mitra.presentation.dto.request.AddRoutineExerciseRequestDto;
import com.mitra.presentation.dto.response.RoutineExerciseResponseDto;

public interface AddRoutineExerciseUseCase {
    RoutineExerciseResponseDto execute(Long userId, Long routineId, AddRoutineExerciseRequestDto request);
}
