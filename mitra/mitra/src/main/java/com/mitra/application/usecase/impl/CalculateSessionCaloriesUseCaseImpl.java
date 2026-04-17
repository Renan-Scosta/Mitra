package com.mitra.application.usecase.impl;

import com.mitra.application.port.out.BodyMeasurementRepositoryPort;
import com.mitra.application.port.out.WorkoutSessionRepositoryPort;
import com.mitra.application.usecase.CalculateSessionCaloriesUseCase;
import com.mitra.domain.model.BodyMeasurement;
import com.mitra.domain.model.WorkoutSession;
import com.mitra.domain.service.CalorieCalculator;
import com.mitra.domain.service.CalorieResult;
import com.mitra.presentation.dto.response.SessionCaloriesResponseDto;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CalculateSessionCaloriesUseCaseImpl implements CalculateSessionCaloriesUseCase {

    private final WorkoutSessionRepositoryPort workoutSessionRepositoryPort;
    private final BodyMeasurementRepositoryPort bodyMeasurementRepositoryPort;
    private final CalorieCalculator calorieCalculator;

    public CalculateSessionCaloriesUseCaseImpl(
            WorkoutSessionRepositoryPort workoutSessionRepositoryPort,
            BodyMeasurementRepositoryPort bodyMeasurementRepositoryPort) {
        this.workoutSessionRepositoryPort = workoutSessionRepositoryPort;
        this.bodyMeasurementRepositoryPort = bodyMeasurementRepositoryPort;
        this.calorieCalculator = new CalorieCalculator();
    }

    @Override
    public SessionCaloriesResponseDto execute(Long userId, Long sessionId) {
        WorkoutSession session = workoutSessionRepositoryPort.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Session not found"));

        if (!session.getUserId().equals(userId)) {
            throw new SecurityException("You do not own this session");
        }

        BodyMeasurement measurement = bodyMeasurementRepositoryPort.findLatestByUserId(userId)
                .orElseThrow(() -> new IllegalStateException("No body measurement found. Please record your weight first."));

        CalorieResult result = calorieCalculator.calculate(session.getSetRecords(), measurement.getWeightKg());

        List<SessionCaloriesResponseDto.ExerciseCaloriesDto> perExerciseDtos = result.perExercise().stream()
                .map(e -> new SessionCaloriesResponseDto.ExerciseCaloriesDto(e.exerciseName(), e.calories()))
                .collect(Collectors.toList());

        return new SessionCaloriesResponseDto(
                sessionId,
                result.totalCalories(),
                perExerciseDtos
        );
    }
}
