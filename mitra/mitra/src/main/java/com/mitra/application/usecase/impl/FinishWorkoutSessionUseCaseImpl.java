package com.mitra.application.usecase.impl;

import com.mitra.application.port.out.WorkoutSessionRepositoryPort;
import com.mitra.application.usecase.FinishWorkoutSessionUseCase;
import com.mitra.domain.model.WorkoutSession;
import com.mitra.presentation.dto.response.SessionSummaryResponseDto;
import org.springframework.stereotype.Service;

@Service
public class FinishWorkoutSessionUseCaseImpl implements FinishWorkoutSessionUseCase {

    private final WorkoutSessionRepositoryPort workoutSessionRepositoryPort;

    public FinishWorkoutSessionUseCaseImpl(WorkoutSessionRepositoryPort workoutSessionRepositoryPort) {
        this.workoutSessionRepositoryPort = workoutSessionRepositoryPort;
    }

    @Override
    public SessionSummaryResponseDto execute(Long userId, Long sessionId) {
        WorkoutSession session = workoutSessionRepositoryPort.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Session not found"));

        if (!session.getUserId().equals(userId)) {
            throw new SecurityException("You do not own this session");
        }

        session.finish();
        WorkoutSession saved = workoutSessionRepositoryPort.save(session);

        int totalSets = saved.getSetRecords() != null ? saved.getSetRecords().size() : 0;
        long durationMinutes = saved.getEffectiveDuration().toMinutes();

        return new SessionSummaryResponseDto(saved.getId(), totalSets, durationMinutes);
    }
}
