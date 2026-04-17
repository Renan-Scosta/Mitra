package com.mitra.application.usecase.impl;

import com.mitra.application.port.out.GoogleTokenVerifierPort;
import com.mitra.application.port.out.UserRepositoryPort;
import com.mitra.domain.model.User;
import com.mitra.infrastructure.security.TokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.ArgumentCaptor;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GoogleLoginUseCaseImplTest {

    @Mock
    private GoogleTokenVerifierPort tokenVerifierPort;
    @Mock
    private UserRepositoryPort userRepositoryPort;
    @Mock
    private TokenService tokenService;

    private GoogleLoginUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        useCase = new GoogleLoginUseCaseImpl(tokenVerifierPort, userRepositoryPort, tokenService);
    }

    @Test
    void shouldReturnTokenWhenUserAlreadyExists() {
        String idToken = "valid-token";
        String email = "existing@google.com";
        User user = User.builder().id(1L).email(email).build();

        when(tokenVerifierPort.verifyToken(idToken)).thenReturn(Optional.of(email));
        when(userRepositoryPort.findByEmail(email)).thenReturn(Optional.of(user));
        when(tokenService.generateToken(email, 1L)).thenReturn("jwt-token");

        String result = useCase.execute(idToken);

        assertEquals("jwt-token", result);
        verify(userRepositoryPort, never()).save(any());
    }

    @Test
    void shouldCreateNewUserAndReturnTokenWhenUserDoesNotExist() {
        String idToken = "valid-token";
        String email = "newuser@google.com";
        User savedUser = User.builder().id(2L).email(email).build();

        when(tokenVerifierPort.verifyToken(idToken)).thenReturn(Optional.of(email));
        when(userRepositoryPort.findByEmail(email)).thenReturn(Optional.empty());
        when(userRepositoryPort.save(any(User.class))).thenReturn(savedUser);
        when(tokenService.generateToken(email, 2L)).thenReturn("new-jwt-token");

        String result = useCase.execute(idToken);

        assertEquals("new-jwt-token", result);
        
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepositoryPort).save(userCaptor.capture());
        
        User capturedParams = userCaptor.getValue();
        assertEquals(email, capturedParams.getEmail());
        assertEquals("newuser", capturedParams.getName());
        assertEquals("[OAUTH2_GOOGLE]", capturedParams.getPassword());
    }

    @Test
    void shouldThrowExceptionWhenTokenIsInvalid() {
        String idToken = "invalid-token";
        when(tokenVerifierPort.verifyToken(idToken)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> useCase.execute(idToken));
        
        verify(userRepositoryPort, never()).findByEmail(any());
    }
}
