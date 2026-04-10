package com.mitra.application.usecase.impl;

import com.mitra.application.usecase.StartWorkoutSessionUseCase;
import com.mitra.presentation.dto.request.StartSessionRequestDto;
import org.springframework.stereotype.Service;

import com.mitra.application.port.out.WorkoutRoutineRepositoryPort;
import com.mitra.application.port.out.WorkoutSessionRepositoryPort;
import com.mitra.domain.model.WorkoutRoutine;
import com.mitra.domain.model.WorkoutSession;
import java.time.LocalDateTime;

@Service
public class StartWorkoutSessionUseCaseImpl implements StartWorkoutSessionUseCase {

    private final WorkoutSessionRepositoryPort workoutSessionRepositoryPort;
    private final WorkoutRoutineRepositoryPort workoutRoutineRepositoryPort;

    public StartWorkoutSessionUseCaseImpl(WorkoutSessionRepositoryPort workoutSessionRepositoryPort,
                                          WorkoutRoutineRepositoryPort workoutRoutineRepositoryPort) {
        this.workoutSessionRepositoryPort = workoutSessionRepositoryPort;
        this.workoutRoutineRepositoryPort = workoutRoutineRepositoryPort;
    }

    @Override
    public Long execute(StartSessionRequestDto request) {
        WorkoutRoutine routine = workoutRoutineRepositoryPort.findById(request.routineId())
                .orElseThrow(() -> new IllegalArgumentException("Routine not found"));
        
        WorkoutSession session = WorkoutSession.builder()
                .userId(request.userId())
                .routineId(routine.getId())
                .startTime(LocalDateTime.now())
                .build();
                
        WorkoutSession saved = workoutSessionRepositoryPort.save(session);
        return saved.getId();
    }
}
