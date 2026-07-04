package com.mitra.application.usecase;

import com.mitra.presentation.dto.request.UpdateUserPasswordRequestDto;

public interface UpdateUserPasswordUseCase {
    void execute(Long userId, UpdateUserPasswordRequestDto request);
}
