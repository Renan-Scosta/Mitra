package com.mitra.infrastructure.persistence.mapper;

import com.mitra.domain.model.WorkoutSession;
import com.mitra.infrastructure.persistence.entity.UserEntity;
import com.mitra.infrastructure.persistence.entity.WorkoutRoutineEntity;
import com.mitra.infrastructure.persistence.entity.WorkoutSessionEntity;

import java.util.stream.Collectors;

public final class WorkoutSessionMapper {

    private WorkoutSessionMapper() {}

    public static WorkoutSession toDomain(WorkoutSessionEntity entity) {
        return WorkoutSession.builder()
                .id(entity.getId())
                .routineId(entity.getWorkoutRoutine().getId())
                .userId(entity.getUser().getId())
                .startTime(entity.getStartTime())
                .endTime(entity.getEndTime())
                .setRecords(
                        entity.getSetRecords().stream()
                                .map(SetRecordMapper::toDomain)
                                .collect(Collectors.toList()))
                .build();
    }

    public static WorkoutSessionEntity toEntity(
            WorkoutSession domain,
            UserEntity userEntity,
            WorkoutRoutineEntity routineEntity) {
        return WorkoutSessionEntity.builder()
                .id(domain.getId())
                .workoutRoutine(routineEntity)
                .user(userEntity)
                .startTime(domain.getStartTime())
                .endTime(domain.getEndTime())
                .build();
    }
}
