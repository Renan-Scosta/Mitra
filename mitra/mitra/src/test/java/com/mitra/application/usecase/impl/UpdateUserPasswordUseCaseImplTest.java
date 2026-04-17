package com.mitra.application.usecase.impl;

import com.mitra.application.exception.ResourceNotFoundException;
import com.mitra.application.port.out.PasswordEncoderPort;
import com.mitra.application.port.out.UserRepositoryPort;
import com.mitra.domain.model.User;
import com.mitra.presentation.dto.request.UpdateUserPasswordRequestDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateUserPasswordUseCaseImplTest {

    @Mock
    private UserRepositoryPort userRepositoryPort;

    @Mock
    private PasswordEncoderPort passwordEncoderPort;

    private UpdateUserPasswordUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        useCase = new UpdateUserPasswordUseCaseImpl(userRepositoryPort, passwordEncoderPort);
    }

    @Test
    void shouldUpdatePassword() {
        Long userId = 1L;
        UpdateUserPasswordRequestDto request = new UpdateUserPasswordRequestDto("old", "newPass", "newPass");
        User user = User.builder().id(userId).password("encodedOld").build();

        when(userRepositoryPort.findById(userId)).thenReturn(Optional.of(user));
        when(passwordEncoderPort.matches("old", "encodedOld")).thenReturn(true);
        when(passwordEncoderPort.encode("newPass")).thenReturn("encodedNew");

        useCase.execute(userId, request);

        verify(userRepositoryPort).save(argThat(savedUser -> "encodedNew".equals(savedUser.getPassword())));
    }

    @Test
    void shouldThrowExceptionWhenPasswordsDoNotMatch() {
        Long userId = 1L;
        UpdateUserPasswordRequestDto request = new UpdateUserPasswordRequestDto("old", "newPass", "diffPass");

        assertThrows(IllegalArgumentException.class, () -> useCase.execute(userId, request));
        verify(userRepositoryPort, never()).findById(anyLong());
    }

    @Test
    void shouldThrowExceptionWhenCurrentPasswordIncorrect() {
        Long userId = 1L;
        UpdateUserPasswordRequestDto request = new UpdateUserPasswordRequestDto("wrong", "newPass", "newPass");
        User user = User.builder().id(userId).password("encodedOld").build();

        when(userRepositoryPort.findById(userId)).thenReturn(Optional.of(user));
        when(passwordEncoderPort.matches("wrong", "encodedOld")).thenReturn(false);

        assertThrows(SecurityException.class, () -> useCase.execute(userId, request));
        verify(userRepositoryPort, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenGoogleOAuth2ManagedAccount() {
        Long userId = 1L;
        UpdateUserPasswordRequestDto request = new UpdateUserPasswordRequestDto("old", "newPass", "newPass");
        User user = User.builder().id(userId).password("[OAUTH2_GOOGLE]").build();

        when(userRepositoryPort.findById(userId)).thenReturn(Optional.of(user));

        assertThrows(SecurityException.class, () -> useCase.execute(userId, request));
        verify(passwordEncoderPort, never()).matches(anyString(), anyString());
    }
}
