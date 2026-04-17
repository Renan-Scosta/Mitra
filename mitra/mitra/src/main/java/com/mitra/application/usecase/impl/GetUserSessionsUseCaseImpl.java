package com.mitra.application.usecase.impl;

import com.mitra.application.port.out.WorkoutSessionRepositoryPort;
import com.mitra.application.usecase.GetUserSessionsUseCase;
import com.mitra.domain.model.WorkoutSession;
import com.mitra.presentation.dto.response.SetRecordResponseDto;
import com.mitra.presentation.dto.response.WorkoutSessionResponseDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GetUserSessionsUseCaseImpl implements GetUserSessionsUseCase {

    private final WorkoutSessionRepositoryPort workoutSessionRepositoryPort;

    public GetUserSessionsUseCaseImpl(WorkoutSessionRepositoryPort workoutSessionRepositoryPort) {
        this.workoutSessionRepositoryPort = workoutSessionRepositoryPort;
    }

    @Override
    @Transactional(readOnly = true)
    public List<WorkoutSessionResponseDto> execute(Long userId) {
        return workoutSessionRepositoryPort.findByUserId(userId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    private WorkoutSessionResponseDto toDto(WorkoutSession session) {
        List<SetRecordResponseDto> setDtos = new ArrayList<>();
        if (session.getSetRecords() != null) {
            setDtos = session.getSetRecords().stream()
                    .map(r -> new SetRecordResponseDto(
                            r.getId(), r.getExercise().getId(),
                            r.getWeightKg(), r.getReps(), r.getDurationSeconds()))
                    .collect(Collectors.toList());
        }
        return new WorkoutSessionResponseDto(
                session.getId(), session.getUserId(), session.getRoutineId(),
                session.getStartTime(), session.getEndTime(),
                session.isActive(), setDtos
        );
    }
}
