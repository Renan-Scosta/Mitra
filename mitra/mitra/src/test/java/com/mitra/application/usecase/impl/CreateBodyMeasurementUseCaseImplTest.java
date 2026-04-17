package com.mitra.application.usecase.impl;

import com.mitra.application.port.out.BodyMeasurementRepositoryPort;
import com.mitra.domain.model.BodyMeasurement;
import com.mitra.presentation.dto.request.CreateBodyMeasurementRequestDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateBodyMeasurementUseCaseImplTest {

    @Mock
    private BodyMeasurementRepositoryPort bodyMeasurementRepositoryPort;

    private CreateBodyMeasurementUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        useCase = new CreateBodyMeasurementUseCaseImpl(bodyMeasurementRepositoryPort);
    }

    @Test
    void shouldCreateMeasurementForUser() {
        CreateBodyMeasurementRequestDto request = new CreateBodyMeasurementRequestDto(
                new BigDecimal("82.5"), new BigDecimal("19.0"), LocalDate.of(2026, 4, 16)
        );

        when(bodyMeasurementRepositoryPort.save(any(BodyMeasurement.class)))
                .thenReturn(BodyMeasurement.builder().id(10L).userId(1L)
                        .weightKg(new BigDecimal("82.5")).recordDate(LocalDate.of(2026, 4, 16)).build());

        Long id = useCase.execute(1L, request);

        assertEquals(10L, id);
        verify(bodyMeasurementRepositoryPort).save(argThat(bm ->
                bm.getUserId().equals(1L) &&
                bm.getWeightKg().compareTo(new BigDecimal("82.5")) == 0 &&
                bm.getBodyFatPercentage().compareTo(new BigDecimal("19.0")) == 0
        ));
    }

    @Test
    void shouldCreateMeasurementWithoutBodyFat() {
        CreateBodyMeasurementRequestDto request = new CreateBodyMeasurementRequestDto(
                new BigDecimal("80.0"), null, LocalDate.of(2026, 4, 16)
        );

        when(bodyMeasurementRepositoryPort.save(any(BodyMeasurement.class)))
                .thenReturn(BodyMeasurement.builder().id(11L).userId(1L)
                        .weightKg(new BigDecimal("80.0")).recordDate(LocalDate.of(2026, 4, 16)).build());

        Long id = useCase.execute(1L, request);

        assertEquals(11L, id);
        verify(bodyMeasurementRepositoryPort).save(argThat(bm ->
                bm.getBodyFatPercentage() == null
        ));
    }
}
