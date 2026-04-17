package com.mitra.application.port.out;

import com.mitra.domain.model.BodyMeasurement;

import java.util.List;
import java.util.Optional;

public interface BodyMeasurementRepositoryPort {
    Optional<BodyMeasurement> findLatestByUserId(Long userId);
    List<BodyMeasurement> findAllByUserId(Long userId);
    BodyMeasurement save(BodyMeasurement bodyMeasurement);
}
