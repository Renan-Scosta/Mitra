package com.mitra.infrastructure.persistence.mapper;

import com.mitra.domain.model.SetRecord;
import com.mitra.infrastructure.persistence.entity.ExerciseEntity;
import com.mitra.infrastructure.persistence.entity.SetRecordEntity;
import com.mitra.infrastructure.persistence.entity.WorkoutSessionEntity;

public final class SetRecordMapper {

    private SetRecordMapper() {}

    public static SetRecord toDomain(SetRecordEntity entity) {
        return SetRecord.builder()
                .id(entity.getId())
                .sessionId(entity.getWorkoutSession().getId())
                .exercise(ExerciseMapper.toDomain(entity.getExercise()))
                .weightKg(entity.getWeightKg())
                .reps(entity.getReps())
                .durationSeconds(entity.getDurationSeconds())
                .build();
    }

    public static SetRecordEntity toEntity(
            SetRecord domain,
            WorkoutSessionEntity sessionEntity,
            ExerciseEntity exerciseEntity) {
        return SetRecordEntity.builder()
                .id(domain.getId())
                .workoutSession(sessionEntity)
                .exercise(exerciseEntity)
                .weightKg(domain.getWeightKg())
                .reps(domain.getReps())
                .durationSeconds(domain.getDurationSeconds())
                .build();
    }
}
