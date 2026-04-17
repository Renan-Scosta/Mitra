package com.mitra.application.usecase.impl;

import com.mitra.application.port.out.ExerciseRepositoryPort;
import com.mitra.application.port.out.SetRecordRepositoryPort;
import com.mitra.application.usecase.GetPersonalRecordsUseCase;
import com.mitra.domain.model.Exercise;
import com.mitra.domain.model.SetRecord;
import com.mitra.application.exception.ResourceNotFoundException;
import com.mitra.presentation.dto.response.PersonalRecordResponseDto;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@Service
public class GetPersonalRecordsUseCaseImpl implements GetPersonalRecordsUseCase {

    private final ExerciseRepositoryPort exerciseRepositoryPort;
    private final SetRecordRepositoryPort setRecordRepositoryPort;

    public GetPersonalRecordsUseCaseImpl(
            ExerciseRepositoryPort exerciseRepositoryPort,
            SetRecordRepositoryPort setRecordRepositoryPort) {
        this.exerciseRepositoryPort = exerciseRepositoryPort;
        this.setRecordRepositoryPort = setRecordRepositoryPort;
    }

    @Override
    public PersonalRecordResponseDto execute(Long userId, Long exerciseId) {
        Exercise exercise = exerciseRepositoryPort.findById(exerciseId)
                .orElseThrow(() -> new IllegalArgumentException("Exercise not found"));

        List<SetRecord> userSets = setRecordRepositoryPort.findByUserIdAndExerciseId(userId, exerciseId);

        if (userSets.isEmpty()) {
            throw new ResourceNotFoundException("No set records found to calculate PRs for this exercise");
        }

        BigDecimal maxWeight = userSets.stream()
                .map(SetRecord::getWeightKg)
                .filter(Objects::nonNull)
                .max(Comparator.naturalOrder())
                .orElse(null);

        Integer maxReps = userSets.stream()
                .map(SetRecord::getReps)
                .filter(Objects::nonNull)
                .max(Comparator.naturalOrder())
                .orElse(null);

        Integer maxDuration = userSets.stream()
                .map(SetRecord::getDurationSeconds)
                .filter(Objects::nonNull)
                .max(Comparator.naturalOrder())
                .orElse(null);

        BigDecimal maxVolume = userSets.stream()
                .filter(s -> s.getWeightKg() != null && s.getReps() != null)
                .map(s -> s.getWeightKg().multiply(BigDecimal.valueOf(s.getReps())))
                .max(Comparator.naturalOrder())
                .orElse(null);

        return new PersonalRecordResponseDto(
                exerciseId,
                exercise.getName(),
                maxWeight,
                maxReps,
                maxVolume,
                maxDuration,
                userSets.size()
        );
    }
}
