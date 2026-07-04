package com.mitra.application.usecase.impl;

import com.mitra.application.port.out.WorkoutRoutineRepositoryPort;
import com.mitra.application.usecase.CreateWorkoutRoutineUseCase;
import com.mitra.domain.model.WorkoutRoutine;
import com.mitra.presentation.dto.request.CreateRoutineRequestDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CreateWorkoutRoutineUseCaseImpl implements CreateWorkoutRoutineUseCase {

    private final WorkoutRoutineRepositoryPort workoutRoutineRepositoryPort;

    public CreateWorkoutRoutineUseCaseImpl(
            WorkoutRoutineRepositoryPort workoutRoutineRepositoryPort) {
        this.workoutRoutineRepositoryPort = workoutRoutineRepositoryPort;
    }

    @Override
    public Long execute(Long userId, CreateRoutineRequestDto request) {
        WorkoutRoutine routine =
                WorkoutRoutine.builder().userId(userId).name(request.name()).build();

        WorkoutRoutine savedRoutine = workoutRoutineRepositoryPort.save(routine);
        log.info("Created routine routineId={} for userId={}", savedRoutine.getId(), userId);
        return savedRoutine.getId();
    }
}
