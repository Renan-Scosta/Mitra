package com.mitra.infrastructure.persistence.repository;

import com.mitra.infrastructure.persistence.entity.SetRecordEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SetRecordJpaRepository extends JpaRepository<SetRecordEntity, Long> {
    List<SetRecordEntity> findByWorkoutSessionId(Long sessionId);

    List<SetRecordEntity> findByWorkoutSession_UserIdAndExercise_Id(Long userId, Long exerciseId);
}
