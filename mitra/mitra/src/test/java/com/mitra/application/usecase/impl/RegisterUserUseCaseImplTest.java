package com.mitra.application.usecase.impl;

import com.mitra.application.port.out.BodyMeasurementRepositoryPort;
import com.mitra.application.port.out.PasswordEncoderPort;
import com.mitra.application.port.out.UserRepositoryPort;
import com.mitra.domain.model.User;
import com.mitra.domain.model.enums.Gender;
import com.mitra.presentation.dto.request.CreateUserRequestDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegisterUserUseCaseImplTest {

    @Mock
    private UserRepositoryPort userRepositoryPort;

    @Mock
    private BodyMeasurementRepositoryPort bodyMeasurementRepositoryPort;

    @Mock
    private PasswordEncoderPort passwordEncoderPort;

    private RegisterUserUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        useCase = new RegisterUserUseCaseImpl(userRepositoryPort, bodyMeasurementRepositoryPort, passwordEncoderPort);
    }

    @Test
    void shouldRegisterUserAndSaveInitialMeasurement() {
        CreateUserRequestDto request = new CreateUserRequestDto(
                "Test User", "test@mitra.com", LocalDate.of(2000, 1, 1),
                Gender.MALE, 180, new BigDecimal("80.5"), "password123", "password123"
        );

        when(userRepositoryPort.findByEmail("test@mitra.com")).thenReturn(Optional.empty());
        when(passwordEncoderPort.encode("password123")).thenReturn("$2a$encoded");
        when(userRepositoryPort.save(any(User.class)))
                .thenAnswer(invocation -> {
                    User u = invocation.getArgument(0);
                    return User.builder()
                            .id(1L)
                            .name(u.getName())
                            .email(u.getEmail())
                            .password(u.getPassword())
                            .birthDate(u.getBirthDate())
                            .gender(u.getGender())
                            .heightCm(u.getHeightCm())
                            .build();
                });

        Long userId = useCase.execute(request);

        assertEquals(1L, userId);
        verify(passwordEncoderPort).encode("password123");
        verify(userRepositoryPort).save(argThat(user ->
                user.getPassword().equals("$2a$encoded") &&
                user.getEmail().equals("test@mitra.com")
        ));
        verify(bodyMeasurementRepositoryPort).save(argThat(bm ->
                bm.getUserId().equals(1L) &&
                bm.getWeightKg().compareTo(new BigDecimal("80.5")) == 0
        ));
    }

    @Test
    void shouldEncodePasswordBeforeSaving() {
        CreateUserRequestDto request = new CreateUserRequestDto(
                "User", "u@m.com", LocalDate.of(1990, 5, 10),
                Gender.FEMALE, 165, new BigDecimal("60.0"), "secret", "secret"
        );

        when(userRepositoryPort.findByEmail("u@m.com")).thenReturn(Optional.empty());
        when(passwordEncoderPort.encode("secret")).thenReturn("$2a$hashed");
        when(userRepositoryPort.save(any(User.class)))
                .thenReturn(User.builder().id(2L).password("$2a$hashed").build());

        useCase.execute(request);

        verify(userRepositoryPort).save(argThat(u -> u.getPassword().equals("$2a$hashed")));
    }

    @Test
    void shouldThrowWhenEmailAlreadyRegistered() {
        CreateUserRequestDto request = new CreateUserRequestDto(
                "User", "existing@mitra.com", LocalDate.of(1990, 5, 10),
                Gender.MALE, 175, new BigDecimal("75.0"), "pass123", "pass123"
        );

        when(userRepositoryPort.findByEmail("existing@mitra.com"))
                .thenReturn(Optional.of(User.builder().id(99L).email("existing@mitra.com").build()));

        assertThrows(IllegalStateException.class, () -> useCase.execute(request));
        verify(userRepositoryPort, never()).save(any());
        verify(passwordEncoderPort, never()).encode(any());
    }
}
