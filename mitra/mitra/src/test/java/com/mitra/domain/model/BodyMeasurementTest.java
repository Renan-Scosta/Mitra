package com.mitra.domain.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class BodyMeasurementTest {

    @Test
    void shouldCalculateLeanMass() {
        BodyMeasurement measurement = BodyMeasurement.builder()
                .id(1L)
                .userId(1L)
                .weightKg(new BigDecimal("80.00"))
                .bodyFatPercentage(new BigDecimal("20.00"))
                .recordDate(LocalDate.now())
                .build();

        Optional<BigDecimal> leanMass = measurement.getLeanMass();

        assertTrue(leanMass.isPresent());
        assertEquals(0, new BigDecimal("64.00").compareTo(leanMass.get()));
    }

    @Test
    void shouldCalculateFatMass() {
        BodyMeasurement measurement = BodyMeasurement.builder()
                .id(1L)
                .userId(1L)
                .weightKg(new BigDecimal("80.00"))
                .bodyFatPercentage(new BigDecimal("20.00"))
                .recordDate(LocalDate.now())
                .build();

        Optional<BigDecimal> fatMass = measurement.getFatMass();

        assertTrue(fatMass.isPresent());
        assertEquals(0, new BigDecimal("16.00").compareTo(fatMass.get()));
    }

    @Test
    void shouldReturnEmptyLeanMassWhenBodyFatIsNull() {
        BodyMeasurement measurement = BodyMeasurement.builder()
                .weightKg(new BigDecimal("80.00"))
                .build();

        assertTrue(measurement.getLeanMass().isEmpty());
    }

    @Test
    void shouldReturnEmptyFatMassWhenBodyFatIsNull() {
        BodyMeasurement measurement = BodyMeasurement.builder()
                .weightKg(new BigDecimal("80.00"))
                .build();

        assertTrue(measurement.getFatMass().isEmpty());
    }

    @Test
    void shouldHandleHighBodyFatPercentage() {
        BodyMeasurement measurement = BodyMeasurement.builder()
                .weightKg(new BigDecimal("100.00"))
                .bodyFatPercentage(new BigDecimal("35.50"))
                .recordDate(LocalDate.now())
                .build();

        Optional<BigDecimal> leanMass = measurement.getLeanMass();
        Optional<BigDecimal> fatMass = measurement.getFatMass();

        assertTrue(leanMass.isPresent());
        assertTrue(fatMass.isPresent());
        assertEquals(0, new BigDecimal("64.50").compareTo(leanMass.get()));
        assertEquals(0, new BigDecimal("35.50").compareTo(fatMass.get()));
    }
}
