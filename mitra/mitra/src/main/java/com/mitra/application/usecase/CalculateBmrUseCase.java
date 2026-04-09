package com.mitra.application.usecase;

import com.mitra.application.port.out.BodyMeasurementRepositoryPort;
import com.mitra.application.port.out.UserRepositoryPort;
import com.mitra.domain.model.BodyMeasurement;
import com.mitra.domain.model.User;
import com.mitra.domain.service.BmrCalculator;

/**
 * Use case: calculates the Basal Metabolic Rate (BMR) for a given user.
 *
 * <p>Orchestrates the retrieval of the user profile and their latest body measurement,
 * then delegates the calculation to the {@link BmrCalculator} domain service.
 */
public class CalculateBmrUseCase {

    private final UserRepositoryPort userRepository;
    private final BodyMeasurementRepositoryPort bodyMeasurementRepository;
    private final BmrCalculator bmrCalculator;

    public CalculateBmrUseCase(
            UserRepositoryPort userRepository,
            BodyMeasurementRepositoryPort bodyMeasurementRepository,
            BmrCalculator bmrCalculator) {
        this.userRepository = userRepository;
        this.bodyMeasurementRepository = bodyMeasurementRepository;
        this.bmrCalculator = bmrCalculator;
    }

    /**
     * @param userId the ID of the user to calculate BMR for
     * @return BMR in kcal/day
     * @throws IllegalArgumentException if the user or a body measurement is not found
     */
    public double execute(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        BodyMeasurement latestMeasurement = bodyMeasurementRepository.findLatestByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No body measurement found for user: " + userId));

        return bmrCalculator.calculate(user, latestMeasurement.getWeightKg());
    }
}
