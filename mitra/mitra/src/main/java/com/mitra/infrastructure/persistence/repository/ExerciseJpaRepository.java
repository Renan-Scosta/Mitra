package com.mitra.infrastructure.persistence.repository;

import com.mitra.infrastructure.persistence.entity.ExerciseEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExerciseJpaRepository extends JpaRepository<ExerciseEntity, Long> {
}
