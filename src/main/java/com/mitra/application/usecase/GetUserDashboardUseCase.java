package com.mitra.application.usecase;

import com.mitra.presentation.dto.response.DashboardResponseDto;

public interface GetUserDashboardUseCase {
    DashboardResponseDto execute(Long userId);
}
