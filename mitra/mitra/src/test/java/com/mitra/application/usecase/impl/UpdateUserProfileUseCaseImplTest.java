package com.mitra.application.usecase.impl;

import com.mitra.application.exception.ResourceNotFoundException;
import com.mitra.application.port.out.UserRepositoryPort;
import com.mitra.domain.model.User;
import com.mitra.domain.model.enums.Gender;
import com.mitra.presentation.dto.request.UpdateUserProfileRequestDto;
import com.mitra.presentation.dto.response.UserProfileResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateUserProfileUseCaseImplTest {

    @Mock
    private UserRepositoryPort userRepositoryPort;

    private UpdateUserProfileUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        useCase = new UpdateUserProfileUseCaseImpl(userRepositoryPort);
    }

    @Test
    void shouldUpdateProfileAndReturnDto() {
        Long userId = 1L;
        UpdateUserProfileRequestDto request = new UpdateUserProfileRequestDto("New Name", LocalDate.of(1995, 1, 1), Gender.FEMALE, 170);

        User existingUser = User.builder().id(userId).email("test@mail.com").name("Old Name").birthDate(LocalDate.of(1990, 1, 1)).gender(Gender.MALE).heightCm(180).password("pass").build();
        User updatedUser = User.builder().id(userId).email("test@mail.com").name("New Name").birthDate(LocalDate.of(1995, 1, 1)).gender(Gender.FEMALE).heightCm(170).password("pass").build();

        when(userRepositoryPort.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepositoryPort.save(any(User.class))).thenReturn(updatedUser);

        UserProfileResponseDto response = useCase.execute(userId, request);

        assertEquals("New Name", response.name());
        assertEquals(170, response.heightCm());
        assertEquals(Gender.FEMALE, response.gender());
        verify(userRepositoryPort).save(any(User.class));
    }

    @Test
    void shouldThrowExceptionWhenUserNotFound() {
        Long userId = 1L;
        UpdateUserProfileRequestDto request = new UpdateUserProfileRequestDto("New Name", LocalDate.of(1995, 1, 1), Gender.FEMALE, 170);

        when(userRepositoryPort.findById(userId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> useCase.execute(userId, request));
        verify(userRepositoryPort, never()).save(any());
    }
}
