package com.mitra.infrastructure.persistence.repository;

import com.mitra.infrastructure.persistence.entity.RoutineExerciseEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoutineExerciseJpaRepository extends JpaRepository<RoutineExerciseEntity, Long> {
    List<RoutineExerciseEntity> findByWorkoutRoutineId(Long routineId);
}
