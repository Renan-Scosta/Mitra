package com.mitra.domain.service;

import com.mitra.domain.model.SetRecord;
import com.mitra.domain.model.enums.TrackingType;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Domain service to calculate calorie expenditure using Metabolic Equivalent of Task (MET).
 * 
 * Formula: Calories (kcal) = MET × weight_kg × duration_hours
 * Reps time estimation: 2.5 seconds per rep.
 */
public class CalorieCalculator {

    private static final double SECONDS_PER_REP = 2.5;

    public CalorieResult calculate(List<SetRecord> sets, BigDecimal userWeightKg) {
        if (sets == null || sets.isEmpty()) {
            return new CalorieResult(0.0, List.of());
        }
        if (userWeightKg == null || userWeightKg.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("User weight must be positive to calculate calories");
        }

        double weight = userWeightKg.doubleValue();

        // Calculate calories per set and group by exercise name
        Map<String, Double> caloriesByExercise = sets.stream()
                .filter(set -> set.getExercise() != null && set.getExercise().getMetFactor() != null)
                .collect(Collectors.groupingBy(
                        set -> set.getExercise().getName(),
                        Collectors.summingDouble(set -> calculateSetCalories(set, weight))
                ));

        List<CalorieResult.ExerciseCalories> perExercise = caloriesByExercise.entrySet().stream()
                .map(entry -> new CalorieResult.ExerciseCalories(
                        entry.getKey(),
                        Math.round(entry.getValue() * 10.0) / 10.0)) // round to 1 decimal
                .toList();

        double totalCalories = perExercise.stream()
                .mapToDouble(CalorieResult.ExerciseCalories::calories)
                .sum();

        // Fix potential floating point issues slightly and round total
        totalCalories = Math.round(totalCalories * 10.0) / 10.0;

        return new CalorieResult(totalCalories, perExercise);
    }

    private double calculateSetCalories(SetRecord set, double userWeightKg) {
        double met = set.getExercise().getMetFactor().doubleValue();
        double durationSeconds = estimateDurationSeconds(set);
        double durationHours = durationSeconds / 3600.0;
        
        return met * userWeightKg * durationHours;
    }

    private double estimateDurationSeconds(SetRecord set) {
        TrackingType type = set.getExercise().getTrackingType();
        
        if (type == TrackingType.TIME_ONLY) {
            return set.getDurationSeconds() != null ? set.getDurationSeconds() : 0.0;
        } else {
            // WEIGHT_REPS or REPS_ONLY
            return set.getReps() != null ? set.getReps() * SECONDS_PER_REP : 0.0;
        }
    }
}
