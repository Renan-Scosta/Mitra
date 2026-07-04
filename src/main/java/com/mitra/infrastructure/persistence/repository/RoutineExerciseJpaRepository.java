package com.mitra.infrastructure.persistence.repository;

import com.mitra.infrastructure.persistence.entity.RoutineExerciseEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoutineExerciseJpaRepository extends JpaRepository<RoutineExerciseEntity, Long> {
    List<RoutineExerciseEntity> findByWorkoutRoutineId(Long routineId);
}
