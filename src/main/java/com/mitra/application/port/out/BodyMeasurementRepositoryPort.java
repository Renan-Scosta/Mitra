package com.mitra.application.port.out;

import com.mitra.domain.model.BodyMeasurement;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BodyMeasurementRepositoryPort {
    Optional<BodyMeasurement> findLatestByUserId(Long userId);

    Page<BodyMeasurement> findAllByUserId(Long userId, Pageable pageable);

    BodyMeasurement save(BodyMeasurement bodyMeasurement);
}
