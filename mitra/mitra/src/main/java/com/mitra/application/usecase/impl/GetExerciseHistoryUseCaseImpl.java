package com.mitra.application.usecase.impl;

import com.mitra.application.port.out.ExerciseRepositoryPort;
import com.mitra.application.port.out.SetRecordRepositoryPort;
import com.mitra.application.port.out.WorkoutSessionRepositoryPort;
import com.mitra.application.usecase.GetExerciseHistoryUseCase;
import com.mitra.domain.model.Exercise;
import com.mitra.domain.model.SetRecord;
import com.mitra.domain.model.WorkoutSession;
import com.mitra.presentation.dto.response.ExerciseHistoryResponseDto;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class GetExerciseHistoryUseCaseImpl implements GetExerciseHistoryUseCase {

    private final ExerciseRepositoryPort exerciseRepositoryPort;
    private final SetRecordRepositoryPort setRecordRepositoryPort;
    private final WorkoutSessionRepositoryPort sessionRepositoryPort;

    public GetExerciseHistoryUseCaseImpl(
            ExerciseRepositoryPort exerciseRepositoryPort,
            SetRecordRepositoryPort setRecordRepositoryPort,
            WorkoutSessionRepositoryPort sessionRepositoryPort) {
        this.exerciseRepositoryPort = exerciseRepositoryPort;
        this.setRecordRepositoryPort = setRecordRepositoryPort;
        this.sessionRepositoryPort = sessionRepositoryPort;
    }

    @Override
    public ExerciseHistoryResponseDto execute(Long userId, Long exerciseId) {
        Exercise exercise = exerciseRepositoryPort.findById(exerciseId)
                .orElseThrow(() -> new IllegalArgumentException("Exercise not found"));

        List<SetRecord> userSets = setRecordRepositoryPort.findByUserIdAndExerciseId(userId, exerciseId);

        if (userSets.isEmpty()) {
            return new ExerciseHistoryResponseDto(exerciseId, exercise.getName(), List.of());
        }

        // Group sets by sessionId
        Map<Long, List<SetRecord>> setsBySession = userSets.stream()
                .collect(Collectors.groupingBy(SetRecord::getSessionId));

        // Fetch all relevant sessions to get their start times
        // We do this by hitting the repository over the distinct session IDs
        // In a real high-perf scenario, a JOIN query would be more efficient, but since user histories
        // per exercise aren't massive, this is acceptable. We'll map them by ID.
        Map<Long, WorkoutSession> sessionsMap = setsBySession.keySet().stream()
                .map(sessionRepositoryPort::findById)
                .filter(java.util.Optional::isPresent)
                .map(java.util.Optional::get)
                .collect(Collectors.toMap(WorkoutSession::getId, Function.identity()));

        List<ExerciseHistoryResponseDto.SessionHistoryDto> sessionDtos = new ArrayList<>();

        for (Map.Entry<Long, List<SetRecord>> entry : setsBySession.entrySet()) {
            Long sessionId = entry.getKey();
            List<SetRecord> sets = entry.getValue();
            WorkoutSession session = sessionsMap.get(sessionId);
            
            if (session != null) {
                List<ExerciseHistoryResponseDto.SetHistoryDto> setDtos = sets.stream()
                        .map(s -> new ExerciseHistoryResponseDto.SetHistoryDto(s.getWeightKg(), s.getReps(), s.getDurationSeconds()))
                        .toList();

                sessionDtos.add(new ExerciseHistoryResponseDto.SessionHistoryDto(
                        sessionId,
                        session.getStartTime(),
                        setDtos
                ));
            }
        }

        // Sort descending by date (most recent first)
        sessionDtos.sort(Comparator.comparing(ExerciseHistoryResponseDto.SessionHistoryDto::date).reversed());

        return new ExerciseHistoryResponseDto(
                exerciseId,
                exercise.getName(),
                sessionDtos
        );
    }
}
