package com.mitra.application.usecase.impl;

import com.mitra.application.port.out.WorkoutSessionRepositoryPort;
import com.mitra.application.usecase.GetUserVolumeSummaryUseCase;
import com.mitra.domain.model.WorkoutSession;
import com.mitra.presentation.dto.response.VolumeSummaryResponseDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class GetUserVolumeSummaryUseCaseImpl implements GetUserVolumeSummaryUseCase {

    private final WorkoutSessionRepositoryPort sessionRepositoryPort;

    public GetUserVolumeSummaryUseCaseImpl(WorkoutSessionRepositoryPort sessionRepositoryPort) {
        this.sessionRepositoryPort = sessionRepositoryPort;
    }

    @Override
    public List<VolumeSummaryResponseDto> execute(Long userId, LocalDateTime startDate, LocalDateTime endDate) {
        List<WorkoutSession> sessionsInRange = sessionRepositoryPort.findByUserId(userId).stream()
                .filter(s -> s.getStartTime() != null && !s.getStartTime().isBefore(startDate) && !s.getStartTime().isAfter(endDate))
                .toList();

        Map<String, Double> volumeByMuscleGroup = sessionsInRange.stream()
                .flatMap(s -> s.getSetRecords().stream())
                .filter(set -> set.getWeightKg() != null && set.getReps() != null)
                .collect(Collectors.groupingBy(
                        set -> set.getExercise() != null && set.getExercise().getMuscleGroup() != null ? set.getExercise().getMuscleGroup() : "UNKNOWN",
                        Collectors.summingDouble(set -> set.getWeightKg().doubleValue() * set.getReps())
                ));

        return volumeByMuscleGroup.entrySet().stream()
                .map(entry -> new VolumeSummaryResponseDto(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }
}
