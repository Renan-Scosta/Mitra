package com.mitra.application.usecase;

import com.mitra.presentation.dto.response.WorkoutSessionResponseDto;
import java.time.LocalDate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface GetUserSessionsUseCase {
    Page<WorkoutSessionResponseDto> execute(
            Long userId, LocalDate startDate, LocalDate endDate, Pageable pageable);
}
