package com.mitra.domain.service;

import com.mitra.domain.model.Exercise;
import com.mitra.domain.model.SetRecord;
import com.mitra.domain.model.enums.TrackingType;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CalorieCalculatorTest {

    private final CalorieCalculator calculator = new CalorieCalculator();
    private final BigDecimal weight = new BigDecimal("80.0"); // 80 kg user

    @Test
    void shouldCalculateCaloriesForWeightRepsExercise() {
        Exercise squat = Exercise.builder()
                .name("Squat")
                .metFactor(new BigDecimal("7.0"))
                .trackingType(TrackingType.WEIGHT_REPS)
                .build();
        
        // 10 reps @ 2.5s = 25 seconds
        // 7.0 MET * 80 kg * (25 / 3600) hours = 3.888... kcal
        SetRecord record = SetRecord.builder().exercise(squat).reps(10).build();

        CalorieResult result = calculator.calculate(List.of(record), weight);

        assertEquals(3.9, result.totalCalories()); // Rounded to 1 decimal place
        assertEquals(1, result.perExercise().size());
        assertEquals("Squat", result.perExercise().get(0).exerciseName());
        assertEquals(3.9, result.perExercise().get(0).calories());
    }

    @Test
    void shouldCalculateCaloriesForTimeOnlyExercise() {
        Exercise plank = Exercise.builder()
                .name("Plank")
                .metFactor(new BigDecimal("3.0"))
                .trackingType(TrackingType.TIME_ONLY)
                .build();
        
        // 60 seconds
        // 3.0 MET * 80 kg * (60 / 3600) hours = 4.0 kcal
        SetRecord record = SetRecord.builder().exercise(plank).durationSeconds(60).build();

        CalorieResult result = calculator.calculate(List.of(record), weight);

        assertEquals(4.0, result.totalCalories());
    }

    @Test
    void shouldCalculateCaloriesForRepsOnlyExercise() {
        Exercise pushup = Exercise.builder()
                .name("Push-up")
                .metFactor(new BigDecimal("5.0"))
                .trackingType(TrackingType.REPS_ONLY)
                .build();
        
        // 20 reps @ 2.5s = 50 seconds
        // 5.0 MET * 80 kg * (50 / 3600) hours = 5.555... kcal
        SetRecord record = SetRecord.builder().exercise(pushup).reps(20).build();

        CalorieResult result = calculator.calculate(List.of(record), weight);

        assertEquals(5.6, result.totalCalories());
    }

    @Test
    void shouldAggregateCaloriesPerExercise() {
        Exercise deadlift = Exercise.builder().name("Deadlift").metFactor(new BigDecimal("6.0")).trackingType(TrackingType.WEIGHT_REPS).build();
        Exercise pullup = Exercise.builder().name("Pull-up").metFactor(new BigDecimal("5.0")).trackingType(TrackingType.REPS_ONLY).build();

        // DL: 10 reps (25s) -> 6.0 * 80 * 25/3600 = 3.33 kcal  -> rounded 3.3
        // DL:  8 reps (20s) -> 6.0 * 80 * 20/3600 = 2.66 kcal  -> rounded 2.7
        // PU: 15 reps (37.5s) -> 5.0 * 80 * 37.5/3600 = 4.16 kcal -> rounded 4.2
        SetRecord s1 = SetRecord.builder().exercise(deadlift).reps(10).build();
        SetRecord s2 = SetRecord.builder().exercise(deadlift).reps(8).build();
        SetRecord s3 = SetRecord.builder().exercise(pullup).reps(15).build();

        CalorieResult result = calculator.calculate(List.of(s1, s2, s3), weight);

        assertEquals(10.2, result.totalCalories(), 0.1); 
        assertEquals(2, result.perExercise().size());

        double dlCalculated = result.perExercise().stream()
                .filter(e -> e.exerciseName().equals("Deadlift"))
                .findFirst().get().calories();
        assertEquals(6.0, dlCalculated, 0.1); // ~6.0 total for DL (math internally groups first, then rounds)

        double puCalculated = result.perExercise().stream()
                .filter(e -> e.exerciseName().equals("Pull-up"))
                .findFirst().get().calories();
        assertEquals(4.2, puCalculated, 0.1); // ~4.2 for Pull-up
    }

    @Test
    void shouldReturnZeroForEmptySetList() {
        CalorieResult result = calculator.calculate(List.of(), weight);
        assertEquals(0.0, result.totalCalories());
        assertTrue(result.perExercise().isEmpty());
    }
}
