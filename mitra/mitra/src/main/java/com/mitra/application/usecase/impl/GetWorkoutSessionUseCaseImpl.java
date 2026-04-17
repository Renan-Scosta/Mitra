package com.mitra.application.usecase.impl;

import com.mitra.application.port.out.WorkoutSessionRepositoryPort;
import com.mitra.application.usecase.GetWorkoutSessionUseCase;
import com.mitra.domain.model.WorkoutSession;
import com.mitra.presentation.dto.response.SetRecordResponseDto;
import com.mitra.presentation.dto.response.WorkoutSessionResponseDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GetWorkoutSessionUseCaseImpl implements GetWorkoutSessionUseCase {

    private final WorkoutSessionRepositoryPort workoutSessionRepositoryPort;

    public GetWorkoutSessionUseCaseImpl(WorkoutSessionRepositoryPort workoutSessionRepositoryPort) {
        this.workoutSessionRepositoryPort = workoutSessionRepositoryPort;
    }

    @Override
    @Transactional(readOnly = true)
    public WorkoutSessionResponseDto execute(Long userId, Long sessionId) {
        WorkoutSession session = workoutSessionRepositoryPort.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Session not found"));

        if (!session.getUserId().equals(userId)) {
            throw new SecurityException("You do not own this session");
        }

        List<SetRecordResponseDto> setDtos = new ArrayList<>();
        if (session.getSetRecords() != null) {
            setDtos = session.getSetRecords().stream()
                    .map(r -> new SetRecordResponseDto(
                            r.getId(),
                            r.getExercise().getId(),
                            r.getWeightKg(),
                            r.getReps(),
                            r.getDurationSeconds()
                    ))
                    .collect(Collectors.toList());
        }

        return new WorkoutSessionResponseDto(
                session.getId(),
                session.getUserId(),
                session.getRoutineId(),
                session.getStartTime(),
                session.getEndTime(),
                session.isActive(),
                setDtos
        );
    }
}
