package com.mitra.application.usecase.impl;

import com.mitra.application.port.out.BodyMeasurementRepositoryPort;
import com.mitra.application.port.out.WorkoutSessionRepositoryPort;
import com.mitra.application.usecase.FinishWorkoutSessionUseCase;
import com.mitra.domain.model.BodyMeasurement;
import com.mitra.domain.model.WorkoutSession;
import com.mitra.domain.service.CalorieCalculator;
import com.mitra.domain.service.CalorieResult;
import com.mitra.presentation.dto.response.SessionSummaryResponseDto;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class FinishWorkoutSessionUseCaseImpl implements FinishWorkoutSessionUseCase {

    private final WorkoutSessionRepositoryPort workoutSessionRepositoryPort;
    private final BodyMeasurementRepositoryPort bodyMeasurementRepositoryPort;
    private final CalorieCalculator calorieCalculator;

    public FinishWorkoutSessionUseCaseImpl(
            WorkoutSessionRepositoryPort workoutSessionRepositoryPort,
            BodyMeasurementRepositoryPort bodyMeasurementRepositoryPort) {
        this.workoutSessionRepositoryPort = workoutSessionRepositoryPort;
        this.bodyMeasurementRepositoryPort = bodyMeasurementRepositoryPort;
        this.calorieCalculator = new CalorieCalculator();
    }

    @Override
    public SessionSummaryResponseDto execute(Long userId, Long sessionId) {
        WorkoutSession session =
                workoutSessionRepositoryPort
                        .findById(sessionId)
                        .orElseThrow(() -> new IllegalArgumentException("Session not found"));

        if (!session.getUserId().equals(userId)) {
            log.warn("Ownership violation: userId={} tried sessionId={}", userId, sessionId);
            throw new SecurityException("You do not own this session");
        }

        session.finish();
        WorkoutSession saved = workoutSessionRepositoryPort.save(session);

        int totalSets = saved.getSetRecords() != null ? saved.getSetRecords().size() : 0;
        long durationMinutes = saved.getEffectiveDuration().toMinutes();

        // Calculate calories gracefully
        Double estimatedCalories = null;
        Optional<BodyMeasurement> measurementOpt =
                bodyMeasurementRepositoryPort.findLatestByUserId(userId);
        if (measurementOpt.isPresent()
                && saved.getSetRecords() != null
                && !saved.getSetRecords().isEmpty()) {
            CalorieResult result =
                    calorieCalculator.calculate(
                            saved.getSetRecords(), measurementOpt.get().getWeightKg());
            estimatedCalories = result.totalCalories();
        }

        log.info(
                "Finished session sessionId={} — sets={} duration={}min calories={}",
                saved.getId(),
                totalSets,
                durationMinutes,
                estimatedCalories);
        return new SessionSummaryResponseDto(
                saved.getId(), totalSets, durationMinutes, estimatedCalories);
    }
}
