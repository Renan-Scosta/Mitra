package com.mitra.application.usecase.impl;

import com.mitra.application.port.out.WorkoutSessionRepositoryPort;
import com.mitra.application.usecase.GetWorkoutSessionUseCase;
import com.mitra.domain.model.WorkoutSession;
import com.mitra.presentation.dto.response.SetRecordResponseDto;
import com.mitra.presentation.dto.response.WorkoutSessionResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class GetWorkoutSessionUseCaseImpl implements GetWorkoutSessionUseCase {

    private final WorkoutSessionRepositoryPort workoutSessionRepositoryPort;

    public GetWorkoutSessionUseCaseImpl(WorkoutSessionRepositoryPort workoutSessionRepositoryPort) {
        this.workoutSessionRepositoryPort = workoutSessionRepositoryPort;
    }

    @Override
    @Transactional(readOnly = true)
    public WorkoutSessionResponseDto execute(Long userId, Long sessionId) {
        log.debug("Fetching session sessionId={} for userId={}", sessionId, userId);
        WorkoutSession session = workoutSessionRepositoryPort.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Session not found"));

        if (!session.getUserId().equals(userId)) {
            log.warn("Ownership violation: userId={} tried sessionId={}", userId, sessionId);
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
