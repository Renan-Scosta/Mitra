package com.mitra.domain.service;

import java.util.List;

public record CalorieResult(
        double totalCalories,
        List<ExerciseCalories> perExercise
) {
    public record ExerciseCalories(String exerciseName, double calories) {}
}
