package com.mitra.presentation.dto.response;

public record DashboardResponseDto(
        int workoutsThisWeek,
        int currentStreak,
        double totalCaloriesThisWeek,
        WorkoutSessionResponseDto lastWorkout
) {}
