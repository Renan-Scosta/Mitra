package com.mitra.application.usecase.impl;

import com.mitra.application.port.out.ExerciseRepositoryPort;
import com.mitra.application.usecase.GetAllExercisesUseCase;
import com.mitra.presentation.dto.response.ExerciseResponseDto;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GetAllExercisesUseCaseImpl implements GetAllExercisesUseCase {

    private final ExerciseRepositoryPort exerciseRepositoryPort;

    public GetAllExercisesUseCaseImpl(ExerciseRepositoryPort exerciseRepositoryPort) {
        this.exerciseRepositoryPort = exerciseRepositoryPort;
    }

    @Override
    public List<ExerciseResponseDto> execute() {
        return exerciseRepositoryPort.findAll().stream()
                .map(exercise -> new ExerciseResponseDto(
                        exercise.getId(),
                        exercise.getName(),
                        exercise.getMuscleGroup(),
                        exercise.getMetFactor(),
                        exercise.getTrackingType()
                ))
                .collect(Collectors.toList());
    }
}
