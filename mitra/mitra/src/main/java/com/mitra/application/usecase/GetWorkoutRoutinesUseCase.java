package com.mitra.application.usecase;

import com.mitra.presentation.dto.response.RoutineResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface GetWorkoutRoutinesUseCase {
    Page<RoutineResponseDto> execute(Long userId, Pageable pageable);
}
