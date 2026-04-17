package com.mitra.application.usecase;

import com.mitra.presentation.dto.response.VolumeSummaryResponseDto;
import java.time.LocalDateTime;
import java.util.List;

public interface GetUserVolumeSummaryUseCase {
    List<VolumeSummaryResponseDto> execute(Long userId, LocalDateTime startDate, LocalDateTime endDate);
}
