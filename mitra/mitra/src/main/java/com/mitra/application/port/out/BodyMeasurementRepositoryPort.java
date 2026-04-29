package com.mitra.application.port.out;

import com.mitra.domain.model.BodyMeasurement;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface BodyMeasurementRepositoryPort {
    Optional<BodyMeasurement> findLatestByUserId(Long userId);
    Page<BodyMeasurement> findAllByUserId(Long userId, Pageable pageable);
    BodyMeasurement save(BodyMeasurement bodyMeasurement);
}
