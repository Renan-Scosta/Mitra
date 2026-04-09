package com.mitra.infrastructure.persistence.mapper;

import com.mitra.domain.model.BodyMeasurement;
import com.mitra.infrastructure.persistence.entity.BodyMeasurementEntity;
import com.mitra.infrastructure.persistence.entity.UserEntity;

public final class BodyMeasurementMapper {

    private BodyMeasurementMapper() {}

    public static BodyMeasurement toDomain(BodyMeasurementEntity entity) {
        return BodyMeasurement.builder()
                .id(entity.getId())
                .userId(entity.getUser().getId())
                .weightKg(entity.getWeightKg())
                .bodyFatPercentage(entity.getBodyFatPercentage())
                .recordDate(entity.getRecordDate())
                .build();
    }

    public static BodyMeasurementEntity toEntity(BodyMeasurement domain, UserEntity userEntity) {
        return BodyMeasurementEntity.builder()
                .id(domain.getId())
                .user(userEntity)
                .weightKg(domain.getWeightKg())
                .bodyFatPercentage(domain.getBodyFatPercentage())
                .recordDate(domain.getRecordDate())
                .build();
    }
}
