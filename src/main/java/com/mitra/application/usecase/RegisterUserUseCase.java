package com.mitra.application.usecase;

import com.mitra.presentation.dto.request.CreateUserRequestDto;

public interface RegisterUserUseCase {
    Long execute(CreateUserRequestDto request);
}
