package com.mitra.domain.service;

import com.mitra.domain.model.User;
import com.mitra.domain.model.enums.Gender;

import java.math.BigDecimal;

/**
 * Domain service that calculates the Basal Metabolic Rate (BMR)
 * using the Mifflin-St Jeor equation.
 *
 * <p>Formula:
 * <ul>
 *   <li>Male:   BMR = (10 × weight_kg) + (6.25 × height_cm) - (5 × age) + 5</li>
 *   <li>Female: BMR = (10 × weight_kg) + (6.25 × height_cm) - (5 × age) - 161</li>
 * </ul>
 *
 * <p>This class is a pure domain service with zero framework dependencies.
 */
public class BmrCalculator {

    private static final double WEIGHT_FACTOR  = 10.0;
    private static final double HEIGHT_FACTOR  = 6.25;
    private static final double AGE_FACTOR     = 5.0;
    private static final double MALE_CONSTANT  = 5.0;
    private static final double FEMALE_CONSTANT = -161.0;

    /**
     * Calculates the BMR in kcal/day.
     *
     * @param user     the user whose physiology data will be used
     * @param weightKg the user's current weight in kilograms
     * @return BMR in kcal/day
     * @throws IllegalArgumentException if user is null or weight is non-positive
     */
    public double calculate(User user, BigDecimal weightKg) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        if (weightKg == null || weightKg.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Weight must be a positive value");
        }

        double base = (WEIGHT_FACTOR * weightKg.doubleValue())
                + (HEIGHT_FACTOR * user.getHeightCm())
                - (AGE_FACTOR * user.getAge());

        double genderAdjustment = user.getGender() == Gender.MALE ? MALE_CONSTANT : FEMALE_CONSTANT;
        return base + genderAdjustment;
    }
}
