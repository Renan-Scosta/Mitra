package com.mitra.infrastructure.persistence.repository;

import com.mitra.infrastructure.persistence.entity.WorkoutSessionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WorkoutSessionJpaRepository extends JpaRepository<WorkoutSessionEntity, Long> {
    List<WorkoutSessionEntity> findByUserId(Long userId);
    Optional<WorkoutSessionEntity> findByUserIdAndEndTimeIsNull(Long userId);
}
