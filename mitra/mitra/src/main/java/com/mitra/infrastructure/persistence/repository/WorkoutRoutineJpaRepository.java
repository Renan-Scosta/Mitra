package com.mitra.infrastructure.persistence.repository;

import com.mitra.infrastructure.persistence.entity.WorkoutRoutineEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkoutRoutineJpaRepository extends JpaRepository<WorkoutRoutineEntity, Long> {
    Page<WorkoutRoutineEntity> findByUserId(Long userId, Pageable pageable);
}
