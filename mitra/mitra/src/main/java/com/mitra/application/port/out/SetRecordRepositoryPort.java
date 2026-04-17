package com.mitra.application.port.out;

import com.mitra.domain.model.SetRecord;

import java.util.List;

public interface SetRecordRepositoryPort {
    List<SetRecord> findBySessionId(Long sessionId);
    SetRecord save(SetRecord setRecord);
}
