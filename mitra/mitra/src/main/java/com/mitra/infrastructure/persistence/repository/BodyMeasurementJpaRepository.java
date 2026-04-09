package com.mitra.infrastructure.persistence.repository;

import com.mitra.infrastructure.persistence.entity.BodyMeasurementEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BodyMeasurementJpaRepository extends JpaRepository<BodyMeasurementEntity, Long> {
    Optional<BodyMeasurementEntity> findFirstByUserIdOrderByRecordDateDesc(Long userId);
}
