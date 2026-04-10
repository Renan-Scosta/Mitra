package com.mitra.application.usecase;

import com.mitra.presentation.dto.response.RoutineResponseDto;
import java.util.List;

public interface GetWorkoutRoutinesUseCase {
    List<RoutineResponseDto> execute(Long userId);
}
