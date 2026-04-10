package com.mitra.application.usecase;

import com.mitra.presentation.dto.response.ExerciseResponseDto;
import java.util.List;

public interface GetAllExercisesUseCase {
    List<ExerciseResponseDto> execute();
}
