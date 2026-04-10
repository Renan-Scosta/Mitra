package com.mitra.application.usecase;

import com.mitra.presentation.dto.request.LogSetRequestDto;
import com.mitra.presentation.dto.response.SetRecordResponseDto;

public interface LogSetRecordUseCase {
    SetRecordResponseDto execute(Long sessionId, LogSetRequestDto request);
}
