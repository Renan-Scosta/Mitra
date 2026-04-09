package com.mitra.infrastructure.persistence.repository;

import com.mitra.infrastructure.persistence.entity.WorkoutRoutineEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WorkoutRoutineJpaRepository extends JpaRepository<WorkoutRoutineEntity, Long> {
    List<WorkoutRoutineEntity> findByUserId(Long userId);
}
