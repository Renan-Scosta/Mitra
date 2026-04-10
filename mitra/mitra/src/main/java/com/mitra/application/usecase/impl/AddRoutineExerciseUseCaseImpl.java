package com.mitra.application.usecase.impl;

import com.mitra.application.port.out.ExerciseRepositoryPort;
import com.mitra.application.port.out.RoutineExerciseRepositoryPort;
import com.mitra.application.usecase.AddRoutineExerciseUseCase;
import com.mitra.domain.model.Exercise;
import com.mitra.domain.model.RoutineExercise;
import com.mitra.presentation.dto.request.AddRoutineExerciseRequestDto;
import com.mitra.presentation.dto.response.ExerciseResponseDto;
import com.mitra.presentation.dto.response.RoutineExerciseResponseDto;
import org.springframework.stereotype.Service;

@Service
public class AddRoutineExerciseUseCaseImpl implements AddRoutineExerciseUseCase {

    private final RoutineExerciseRepositoryPort routineExerciseRepositoryPort;
    private final ExerciseRepositoryPort exerciseRepositoryPort;

    public AddRoutineExerciseUseCaseImpl(
            RoutineExerciseRepositoryPort routineExerciseRepositoryPort,
            ExerciseRepositoryPort exerciseRepositoryPort) {
        this.routineExerciseRepositoryPort = routineExerciseRepositoryPort;
        this.exerciseRepositoryPort = exerciseRepositoryPort;
    }

    @Override
    public RoutineExerciseResponseDto execute(Long routineId, AddRoutineExerciseRequestDto request) {
        Exercise exercise = exerciseRepositoryPort.findById(request.exerciseId())
                .orElseThrow(() -> new IllegalArgumentException("Exercise not found with id: " + request.exerciseId()));

        RoutineExercise routineExercise = RoutineExercise.builder()
                .routineId(routineId)
                .exercise(exercise)
                .targetSets(request.targetSets())
                .targetReps(request.targetReps())
                .build();
                
        RoutineExercise saved = routineExerciseRepositoryPort.save(routineExercise);

        ExerciseResponseDto exerciseResponse = new ExerciseResponseDto(
                exercise.getId(), exercise.getName(), exercise.getMuscleGroup(),
                exercise.getMetFactor(), exercise.getTrackingType()
        );

        return new RoutineExerciseResponseDto(
                saved.getId(),
                exerciseResponse,
                saved.getTargetSets(),
                saved.getTargetReps()
        );
    }
}
