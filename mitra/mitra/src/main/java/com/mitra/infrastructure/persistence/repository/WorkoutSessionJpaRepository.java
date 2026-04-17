package com.mitra.infrastructure.persistence.repository;

import com.mitra.infrastructure.persistence.entity.WorkoutSessionEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface WorkoutSessionJpaRepository extends JpaRepository<WorkoutSessionEntity, Long> {
    List<WorkoutSessionEntity> findByUserId(Long userId);
    Page<WorkoutSessionEntity> findByUserIdAndStartTimeBetween(Long userId, LocalDateTime start, LocalDateTime end, Pageable pageable);
    Optional<WorkoutSessionEntity> findByUserIdAndEndTimeIsNull(Long userId);
}
