package com.mitra.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Optional;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BodyMeasurement {

    private Long id;
    private Long userId;
    private BigDecimal weightKg;
    private BigDecimal bodyFatPercentage;
    private LocalDate recordDate;

    /**
     * Returns lean (muscle) mass in kg, if body fat percentage is available.
     * Formula: weightKg * (1 - bodyFatPercentage / 100)
     */
    public Optional<BigDecimal> getLeanMass() {
        if (bodyFatPercentage == null) return Optional.empty();
        BigDecimal fraction = bodyFatPercentage.divide(BigDecimal.valueOf(100), 6, RoundingMode.HALF_UP);
        return Optional.of(weightKg.multiply(BigDecimal.ONE.subtract(fraction)).setScale(2, RoundingMode.HALF_UP));
    }

    /**
     * Returns fat mass in kg, if body fat percentage is available.
     * Formula: weightKg * (bodyFatPercentage / 100)
     */
    public Optional<BigDecimal> getFatMass() {
        if (bodyFatPercentage == null) return Optional.empty();
        BigDecimal fraction = bodyFatPercentage.divide(BigDecimal.valueOf(100), 6, RoundingMode.HALF_UP);
        return Optional.of(weightKg.multiply(fraction).setScale(2, RoundingMode.HALF_UP));
    }
}
