package com.mitra.application.usecase.impl;

import com.mitra.application.port.out.WorkoutSessionRepositoryPort;
import com.mitra.application.usecase.GetUserSessionsUseCase;
import com.mitra.domain.model.WorkoutSession;
import com.mitra.presentation.dto.response.SetRecordResponseDto;
import com.mitra.presentation.dto.response.WorkoutSessionResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
    public Page<WorkoutSessionResponseDto> execute(Long userId, LocalDate startDate, LocalDate endDate, Pageable pageable) {
        LocalDate start = startDate != null ? startDate : LocalDate.of(2020, 1, 1);
        LocalDate end = endDate != null ? endDate : LocalDate.now().plusDays(1);
        
        LocalDateTime startDateTime = start.atStartOfDay();
        LocalDateTime endDateTime = end.atTime(23, 59, 59);

        return workoutSessionRepositoryPort.findByUserIdAndDateRange(userId, startDateTime, endDateTime, pageable)
                .map(this::toDto);
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
