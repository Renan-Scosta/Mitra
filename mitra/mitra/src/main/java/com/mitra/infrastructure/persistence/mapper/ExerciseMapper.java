package com.mitra.infrastructure.persistence.mapper;

import com.mitra.domain.model.Exercise;
import com.mitra.infrastructure.persistence.entity.ExerciseEntity;

public final class ExerciseMapper {

    private ExerciseMapper() {}

    public static Exercise toDomain(ExerciseEntity entity) {
        return Exercise.builder()
                .id(entity.getId())
                .name(entity.getName())
                .muscleGroup(entity.getMuscleGroup())
                .metFactor(entity.getMetFactor())
                .trackingType(entity.getTrackingType())
                .build();
    }

    public static ExerciseEntity toEntity(Exercise domain) {
        return ExerciseEntity.builder()
                .id(domain.getId())
                .name(domain.getName())
                .muscleGroup(domain.getMuscleGroup())
                .metFactor(domain.getMetFactor())
                .trackingType(domain.getTrackingType())
                .build();
    }
}
