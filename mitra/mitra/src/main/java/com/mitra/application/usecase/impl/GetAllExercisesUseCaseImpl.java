package com.mitra.application.usecase.impl;

import com.mitra.application.port.out.ExerciseRepositoryPort;
import com.mitra.application.usecase.GetAllExercisesUseCase;
import com.mitra.presentation.dto.response.ExerciseResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class GetAllExercisesUseCaseImpl implements GetAllExercisesUseCase {

    private final ExerciseRepositoryPort exerciseRepositoryPort;

    public GetAllExercisesUseCaseImpl(ExerciseRepositoryPort exerciseRepositoryPort) {
        this.exerciseRepositoryPort = exerciseRepositoryPort;
    }

    @Override
    public Page<ExerciseResponseDto> execute(Pageable pageable) {
        return exerciseRepositoryPort.findAll(pageable)
                .map(exercise -> new ExerciseResponseDto(
                        exercise.getId(),
                        exercise.getName(),
                        exercise.getMuscleGroup(),
                        exercise.getMetFactor(),
                        exercise.getTrackingType()
                ));
    }
}
