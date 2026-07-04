package com.mitra.application.usecase;

import com.mitra.presentation.dto.response.ExerciseResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface GetAllExercisesUseCase {
    Page<ExerciseResponseDto> execute(Pageable pageable);
}
