package com.mitra.infrastructure.persistence.repository;

import com.mitra.infrastructure.persistence.entity.BodyMeasurementEntity;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BodyMeasurementJpaRepository extends JpaRepository<BodyMeasurementEntity, Long> {
    Optional<BodyMeasurementEntity> findFirstByUserIdOrderByRecordDateDesc(Long userId);

    Page<BodyMeasurementEntity> findByUserIdOrderByRecordDateDesc(Long userId, Pageable pageable);
}
