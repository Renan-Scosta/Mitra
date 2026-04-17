package com.mitra.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;

public record GoogleLoginRequestDto(
        @NotBlank(message = "Id token must not be blank") String idToken
) {}
