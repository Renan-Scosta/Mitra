package com.mitra.application.usecase.impl;

import com.mitra.application.port.out.ExerciseRepositoryPort;
import com.mitra.application.usecase.CreateExerciseUseCase;
import com.mitra.domain.model.Exercise;
import com.mitra.presentation.dto.request.CreateExerciseRequestDto;
import org.springframework.stereotype.Service;

@Service
public class CreateExerciseUseCaseImpl implements CreateExerciseUseCase {

    private final ExerciseRepositoryPort exerciseRepositoryPort;

    public CreateExerciseUseCaseImpl(ExerciseRepositoryPort exerciseRepositoryPort) {
        this.exerciseRepositoryPort = exerciseRepositoryPort;
    }

    @Override
    public Long execute(CreateExerciseRequestDto request) {
        Exercise exercise = Exercise.builder()
                .name(request.name())
                .muscleGroup(request.muscleGroup())
                .metFactor(request.metFactor())
                .trackingType(request.trackingType())
                .build();
                
        Exercise savedExercise = exerciseRepositoryPort.save(exercise);
        return savedExercise.getId();
    }
}
