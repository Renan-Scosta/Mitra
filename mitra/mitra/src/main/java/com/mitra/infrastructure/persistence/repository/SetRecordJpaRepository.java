package com.mitra.infrastructure.persistence.repository;

import com.mitra.infrastructure.persistence.entity.SetRecordEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SetRecordJpaRepository extends JpaRepository<SetRecordEntity, Long> {
    List<SetRecordEntity> findByWorkoutSessionId(Long sessionId);
}
