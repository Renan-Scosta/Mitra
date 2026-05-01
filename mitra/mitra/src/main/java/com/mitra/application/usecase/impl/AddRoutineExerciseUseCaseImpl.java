package com.mitra.application.usecase.impl;

import com.mitra.application.port.out.ExerciseRepositoryPort;
import com.mitra.application.port.out.RoutineExerciseRepositoryPort;
import com.mitra.application.port.out.WorkoutRoutineRepositoryPort;
import com.mitra.application.usecase.AddRoutineExerciseUseCase;
import com.mitra.domain.model.Exercise;
import com.mitra.domain.model.RoutineExercise;
import com.mitra.domain.model.WorkoutRoutine;
import com.mitra.presentation.dto.request.AddRoutineExerciseRequestDto;
import com.mitra.presentation.dto.response.ExerciseResponseDto;
import com.mitra.presentation.dto.response.RoutineExerciseResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AddRoutineExerciseUseCaseImpl implements AddRoutineExerciseUseCase {

    private final RoutineExerciseRepositoryPort routineExerciseRepositoryPort;
    private final ExerciseRepositoryPort exerciseRepositoryPort;
    private final WorkoutRoutineRepositoryPort workoutRoutineRepositoryPort;

    public AddRoutineExerciseUseCaseImpl(
            RoutineExerciseRepositoryPort routineExerciseRepositoryPort,
            ExerciseRepositoryPort exerciseRepositoryPort,
            WorkoutRoutineRepositoryPort workoutRoutineRepositoryPort) {
        this.routineExerciseRepositoryPort = routineExerciseRepositoryPort;
        this.exerciseRepositoryPort = exerciseRepositoryPort;
        this.workoutRoutineRepositoryPort = workoutRoutineRepositoryPort;
    }

    @Override
    public RoutineExerciseResponseDto execute(Long userId, Long routineId, AddRoutineExerciseRequestDto request) {
        WorkoutRoutine routine = workoutRoutineRepositoryPort.findById(routineId)
                .orElseThrow(() -> new IllegalArgumentException("Routine not found with id: " + routineId));

        if (!routine.getUserId().equals(userId)) {
            log.warn("Ownership violation: userId={} tried routineId={}", userId, routineId);
            throw new SecurityException("You do not own this routine");
        }

        Exercise exercise = exerciseRepositoryPort.findById(request.exerciseId())
                .orElseThrow(() -> new IllegalArgumentException("Exercise not found with id: " + request.exerciseId()));

        RoutineExercise routineExercise = RoutineExercise.builder()
                .routineId(routineId)
                .exercise(exercise)
                .targetSets(request.targetSets())
                .targetReps(request.targetReps())
                .build();

        RoutineExercise saved = routineExerciseRepositoryPort.save(routineExercise);
        log.info("Added exercise exerciseId={} to routineId={}", request.exerciseId(), routineId);

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
