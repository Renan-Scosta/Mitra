package com.mitra.application.usecase.impl;

import com.mitra.application.port.out.WorkoutSessionRepositoryPort;
import com.mitra.application.usecase.CalculateSessionCaloriesUseCase;
import com.mitra.application.usecase.GetUserDashboardUseCase;
import com.mitra.domain.model.WorkoutSession;
import com.mitra.presentation.dto.response.DashboardResponseDto;
import com.mitra.presentation.dto.response.SessionCaloriesResponseDto;
import com.mitra.presentation.dto.response.WorkoutSessionResponseDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class GetUserDashboardUseCaseImpl implements GetUserDashboardUseCase {

    private final WorkoutSessionRepositoryPort sessionRepositoryPort;
    private final CalculateSessionCaloriesUseCase calculateSessionCaloriesUseCase;

    public GetUserDashboardUseCaseImpl(WorkoutSessionRepositoryPort sessionRepositoryPort,
                                       CalculateSessionCaloriesUseCase calculateSessionCaloriesUseCase) {
        this.sessionRepositoryPort = sessionRepositoryPort;
        this.calculateSessionCaloriesUseCase = calculateSessionCaloriesUseCase;
    }

    @Override
    public DashboardResponseDto execute(Long userId) {
        List<WorkoutSession> allFinishedSessions = sessionRepositoryPort.findByUserId(userId).stream()
                .filter(s -> s.getEndTime() != null) // Only count finished ones for metrics
                .toList();

        // Streak calculation
        List<LocalDate> workoutDates = allFinishedSessions.stream()
                .map(s -> s.getStartTime().toLocalDate())
                .distinct()
                .sorted(Comparator.reverseOrder())
                .collect(Collectors.toList());

        int currentStreak = calculateStreak(workoutDates);

        // This week sessions (last 7 days including today)
        LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);
        List<WorkoutSession> last7DaysSessions = allFinishedSessions.stream()
                .filter(s -> s.getStartTime().isAfter(sevenDaysAgo))
                .toList();
        int workoutsThisWeek = last7DaysSessions.size();

        // Calories this week
        double totalCaloriesThisWeek = 0.0;
        for (WorkoutSession s : last7DaysSessions) {
            SessionCaloriesResponseDto calDto = calculateSessionCaloriesUseCase.execute(userId, s.getId());
            totalCaloriesThisWeek += calDto.totalCalories();
        }

        // Last workout (find the absolute last session, even if it's active)
        List<WorkoutSession> allSessionsInclActive = sessionRepositoryPort.findByUserId(userId);
        WorkoutSessionResponseDto lastWorkoutDto = null;
        if (!allSessionsInclActive.isEmpty()) {
            WorkoutSession lastSession = allSessionsInclActive.stream()
                    .max(Comparator.comparing(WorkoutSession::getStartTime))
                    .orElse(null);

            if (lastSession != null) {
                lastWorkoutDto = new WorkoutSessionResponseDto(
                    lastSession.getId(),
                    lastSession.getUserId(),
                    lastSession.getRoutineId(),
                    lastSession.getStartTime(),
                    lastSession.getEndTime(),
                    lastSession.isActive(),
                    java.util.Collections.emptyList()
                );
            }
        }

        return new DashboardResponseDto(workoutsThisWeek, currentStreak, totalCaloriesThisWeek, lastWorkoutDto);
    }

    private int calculateStreak(List<LocalDate> sortedDatesDesc) {
        if (sortedDatesDesc.isEmpty()) return 0;

        LocalDate today = LocalDate.now();
        LocalDate targetDate = sortedDatesDesc.get(0);

        // Streak must be active either today or yesterday
        if (!targetDate.equals(today) && !targetDate.equals(today.minusDays(1))) {
            return 0;
        }

        int streak = 1;
        for (int i = 1; i < sortedDatesDesc.size(); i++) {
            if (sortedDatesDesc.get(i).equals(targetDate.minusDays(1))) {
                streak++;
                targetDate = sortedDatesDesc.get(i);
            } else {
                break;
            }
        }
        return streak;
    }
}
