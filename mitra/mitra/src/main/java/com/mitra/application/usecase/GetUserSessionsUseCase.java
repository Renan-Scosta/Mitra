package com.mitra.application.usecase;

import com.mitra.presentation.dto.response.WorkoutSessionResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface GetUserSessionsUseCase {
    Page<WorkoutSessionResponseDto> execute(Long userId, LocalDate startDate, LocalDate endDate, Pageable pageable);
}
