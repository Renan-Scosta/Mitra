package com.mitra.application.usecase.impl;

import com.mitra.application.exception.ResourceNotFoundException;
import com.mitra.application.port.out.UserRepositoryPort;
import com.mitra.domain.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteUserAccountUseCaseImplTest {

    @Mock
    private UserRepositoryPort userRepositoryPort;

    private DeleteUserAccountUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        useCase = new DeleteUserAccountUseCaseImpl(userRepositoryPort);
    }

    @Test
    void shouldDeleteAccountWhenUserExists() {
        Long userId = 1L;
        User user = User.builder().id(userId).build();

        when(userRepositoryPort.findById(userId)).thenReturn(Optional.of(user));

        useCase.execute(userId);

        verify(userRepositoryPort).deleteById(userId);
    }

    @Test
    void shouldThrowExceptionWhenUserNotFound() {
        Long userId = 1L;
        when(userRepositoryPort.findById(userId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> useCase.execute(userId));
        verify(userRepositoryPort, never()).deleteById(anyLong());
    }
}
