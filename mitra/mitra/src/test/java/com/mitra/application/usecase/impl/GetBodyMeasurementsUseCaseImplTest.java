package com.mitra.application.usecase.impl;

import com.mitra.application.port.out.BodyMeasurementRepositoryPort;
import com.mitra.domain.model.BodyMeasurement;
import com.mitra.presentation.dto.response.BodyMeasurementResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetBodyMeasurementsUseCaseImplTest {

    @Mock
    private BodyMeasurementRepositoryPort bodyMeasurementRepositoryPort;

    private GetBodyMeasurementsUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        useCase = new GetBodyMeasurementsUseCaseImpl(bodyMeasurementRepositoryPort);
    }

    @Test
    void shouldReturnMeasurementsWithComputedFields() {
        BodyMeasurement bm = BodyMeasurement.builder()
                .id(1L).userId(1L)
                .weightKg(new BigDecimal("80.00"))
                .bodyFatPercentage(new BigDecimal("20.00"))
                .recordDate(LocalDate.of(2026, 4, 16))
                .build();

        when(bodyMeasurementRepositoryPort.findAllByUserId(1L)).thenReturn(List.of(bm));

        List<BodyMeasurementResponseDto> result = useCase.execute(1L);

        assertEquals(1, result.size());
        assertEquals(0, new BigDecimal("64.00").compareTo(result.get(0).leanMassKg()));
        assertEquals(0, new BigDecimal("16.00").compareTo(result.get(0).fatMassKg()));
    }

    @Test
    void shouldReturnNullComputedFieldsWhenNoBodyFat() {
        BodyMeasurement bm = BodyMeasurement.builder()
                .id(2L).userId(1L)
                .weightKg(new BigDecimal("75.00"))
                .recordDate(LocalDate.of(2026, 4, 10))
                .build();

        when(bodyMeasurementRepositoryPort.findAllByUserId(1L)).thenReturn(List.of(bm));

        List<BodyMeasurementResponseDto> result = useCase.execute(1L);

        assertEquals(1, result.size());
        assertNull(result.get(0).leanMassKg());
        assertNull(result.get(0).fatMassKg());
    }

    @Test
    void shouldReturnEmptyListWhenNoMeasurements() {
        when(bodyMeasurementRepositoryPort.findAllByUserId(99L)).thenReturn(List.of());

        List<BodyMeasurementResponseDto> result = useCase.execute(99L);

        assertTrue(result.isEmpty());
    }
}
