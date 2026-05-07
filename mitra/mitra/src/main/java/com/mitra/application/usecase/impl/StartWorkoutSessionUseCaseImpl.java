package com.mitra.application.usecase.impl;

import com.mitra.application.port.out.WorkoutRoutineRepositoryPort;
import com.mitra.application.port.out.WorkoutSessionRepositoryPort;
import com.mitra.application.usecase.StartWorkoutSessionUseCase;
import com.mitra.domain.model.WorkoutRoutine;
import com.mitra.domain.model.WorkoutSession;
import com.mitra.presentation.dto.request.StartSessionRequestDto;
import java.time.LocalDateTime;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class StartWorkoutSessionUseCaseImpl implements StartWorkoutSessionUseCase {

    private final WorkoutSessionRepositoryPort workoutSessionRepositoryPort;
    private final WorkoutRoutineRepositoryPort workoutRoutineRepositoryPort;

    public StartWorkoutSessionUseCaseImpl(
            WorkoutSessionRepositoryPort workoutSessionRepositoryPort,
            WorkoutRoutineRepositoryPort workoutRoutineRepositoryPort) {
        this.workoutSessionRepositoryPort = workoutSessionRepositoryPort;
        this.workoutRoutineRepositoryPort = workoutRoutineRepositoryPort;
    }

    @Override
    public Long execute(Long userId, StartSessionRequestDto request) {
        WorkoutRoutine routine =
                workoutRoutineRepositoryPort
                        .findById(request.routineId())
                        .orElseThrow(() -> new IllegalArgumentException("Routine not found"));

        // Ensure isolation (Routine belongs to the user)
        if (!routine.getUserId().equals(userId)) {
            log.warn(
                    "Ownership violation: userId={} tried routineId={}",
                    userId,
                    request.routineId());
            throw new SecurityException("You do not have permission to execute this routine");
        }

        WorkoutSession session =
                WorkoutSession.builder()
                        .userId(userId)
                        .routineId(routine.getId())
                        .startTime(LocalDateTime.now())
                        .build();

        WorkoutSession saved = workoutSessionRepositoryPort.save(session);
        log.info(
                "Started session sessionId={} for userId={} routineId={}",
                saved.getId(),
                userId,
                routine.getId());
        return saved.getId();
    }
}
