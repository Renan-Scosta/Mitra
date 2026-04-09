package com.mitra.infrastructure.persistence.mapper;

import com.mitra.domain.model.WorkoutRoutine;
import com.mitra.infrastructure.persistence.entity.UserEntity;
import com.mitra.infrastructure.persistence.entity.WorkoutRoutineEntity;

import java.util.stream.Collectors;

public final class WorkoutRoutineMapper {

    private WorkoutRoutineMapper() {}

    public static WorkoutRoutine toDomain(WorkoutRoutineEntity entity) {
        return WorkoutRoutine.builder()
                .id(entity.getId())
                .userId(entity.getUser().getId())
                .name(entity.getName())
                .routineExercises(
                        entity.getRoutineExercises().stream()
                                .map(RoutineExerciseMapper::toDomain)
                                .collect(Collectors.toList()))
                .build();
    }

    public static WorkoutRoutineEntity toEntity(WorkoutRoutine domain, UserEntity userEntity) {
        return WorkoutRoutineEntity.builder()
                .id(domain.getId())
                .user(userEntity)
                .name(domain.getName())
                .build();
    }
}
