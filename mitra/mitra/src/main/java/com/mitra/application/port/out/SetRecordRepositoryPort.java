package com.mitra.application.port.out;

import com.mitra.domain.model.SetRecord;

import java.util.List;

public interface SetRecordRepositoryPort {
    List<SetRecord> findBySessionId(Long sessionId);
    List<SetRecord> findByUserIdAndExerciseId(Long userId, Long exerciseId);
    SetRecord save(SetRecord setRecord);
}
