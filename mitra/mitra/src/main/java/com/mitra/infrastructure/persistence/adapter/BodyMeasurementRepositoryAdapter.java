package com.mitra.infrastructure.persistence.adapter;

import com.mitra.application.port.out.BodyMeasurementRepositoryPort;
import com.mitra.domain.model.BodyMeasurement;
import com.mitra.infrastructure.persistence.mapper.BodyMeasurementMapper;
import com.mitra.infrastructure.persistence.repository.BodyMeasurementJpaRepository;
import com.mitra.infrastructure.persistence.repository.UserJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class BodyMeasurementRepositoryAdapter implements BodyMeasurementRepositoryPort {

    private final BodyMeasurementJpaRepository jpaRepository;
    private final UserJpaRepository userJpaRepository;

    public BodyMeasurementRepositoryAdapter(
            BodyMeasurementJpaRepository jpaRepository,
            UserJpaRepository userJpaRepository) {
        this.jpaRepository = jpaRepository;
        this.userJpaRepository = userJpaRepository;
    }

    @Override
    public Optional<BodyMeasurement> findLatestByUserId(Long userId) {
        return jpaRepository.findFirstByUserIdOrderByRecordDateDesc(userId)
                .map(BodyMeasurementMapper::toDomain);
    }

    @Override
    public BodyMeasurement save(BodyMeasurement bodyMeasurement) {
        var userEntity = userJpaRepository.findById(bodyMeasurement.getUserId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "User not found: " + bodyMeasurement.getUserId()));
        var entity = BodyMeasurementMapper.toEntity(bodyMeasurement, userEntity);
        var saved = jpaRepository.save(entity);
        return BodyMeasurementMapper.toDomain(saved);
    }
}
