package com.mitra.application.usecase;

import com.mitra.presentation.dto.request.UpdateUserProfileRequestDto;
import com.mitra.presentation.dto.response.UserProfileResponseDto;

public interface UpdateUserProfileUseCase {
    UserProfileResponseDto execute(Long userId, UpdateUserProfileRequestDto request);
}
