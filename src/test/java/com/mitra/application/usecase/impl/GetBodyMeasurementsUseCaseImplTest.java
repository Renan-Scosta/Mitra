package com.mitra.application.usecase.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.mitra.application.port.out.BodyMeasurementRepositoryPort;
import com.mitra.domain.model.BodyMeasurement;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GetBodyMeasurementsUseCaseImplTest {

    @Mock private BodyMeasurementRepositoryPort bodyMeasurementRepositoryPort;

    private GetBodyMeasurementsUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        useCase = new GetBodyMeasurementsUseCaseImpl(bodyMeasurementRepositoryPort);
    }

    @Test
    void shouldReturnMeasurementsWithComputedFields() {
        BodyMeasurement bm =
                BodyMeasurement.builder()
                        .id(1L)
                        .userId(1L)
                        .weightKg(new BigDecimal("80.00"))
                        .bodyFatPercentage(new BigDecimal("20.00"))
                        .recordDate(LocalDate.of(2026, 4, 16))
                        .build();

        when(bodyMeasurementRepositoryPort.findAllByUserId(
                        eq(1L), any(org.springframework.data.domain.Pageable.class)))
                .thenReturn(new org.springframework.data.domain.PageImpl<>(List.of(bm)));

        var result = useCase.execute(1L, org.springframework.data.domain.PageRequest.of(0, 10));

        assertEquals(1, result.getTotalElements());
        assertEquals(0, new BigDecimal("64.00").compareTo(result.getContent().get(0).leanMassKg()));
        assertEquals(0, new BigDecimal("16.00").compareTo(result.getContent().get(0).fatMassKg()));
    }

    @Test
    void shouldReturnNullComputedFieldsWhenNoBodyFat() {
        BodyMeasurement bm =
                BodyMeasurement.builder()
                        .id(2L)
                        .userId(1L)
                        .weightKg(new BigDecimal("75.00"))
                        .recordDate(LocalDate.of(2026, 4, 10))
                        .build();

        when(bodyMeasurementRepositoryPort.findAllByUserId(
                        eq(1L), any(org.springframework.data.domain.Pageable.class)))
                .thenReturn(new org.springframework.data.domain.PageImpl<>(List.of(bm)));

        var result = useCase.execute(1L, org.springframework.data.domain.PageRequest.of(0, 10));

        assertEquals(1, result.getTotalElements());
        assertNull(result.getContent().get(0).leanMassKg());
        assertNull(result.getContent().get(0).fatMassKg());
    }

    @Test
    void shouldReturnEmptyListWhenNoMeasurements() {
        when(bodyMeasurementRepositoryPort.findAllByUserId(
                        eq(99L), any(org.springframework.data.domain.Pageable.class)))
                .thenReturn(org.springframework.data.domain.Page.empty());

        var result = useCase.execute(99L, org.springframework.data.domain.PageRequest.of(0, 10));

        assertTrue(result.isEmpty());
    }
}
