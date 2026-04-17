package com.mitra.application.usecase.impl;

import com.mitra.application.exception.ResourceNotFoundException;
import com.mitra.application.port.out.PasswordEncoderPort;
import com.mitra.application.port.out.UserRepositoryPort;
import com.mitra.application.usecase.UpdateUserPasswordUseCase;
import com.mitra.domain.model.User;
import com.mitra.presentation.dto.request.UpdateUserPasswordRequestDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UpdateUserPasswordUseCaseImpl implements UpdateUserPasswordUseCase {

    private final UserRepositoryPort userRepositoryPort;
    private final PasswordEncoderPort passwordEncoderPort;

    public UpdateUserPasswordUseCaseImpl(UserRepositoryPort userRepositoryPort, PasswordEncoderPort passwordEncoderPort) {
        this.userRepositoryPort = userRepositoryPort;
        this.passwordEncoderPort = passwordEncoderPort;
    }

    @Override
    public void execute(Long userId, UpdateUserPasswordRequestDto request) {
        if (!request.newPassword().equals(request.confirmNewPassword())) {
            throw new IllegalArgumentException("New passwords do not match");
        }

        User user = userRepositoryPort.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));

        // Skip password validation if the user was registered via Google OAuth2 with the dummy password
        if ("[OAUTH2_GOOGLE]".equals(user.getPassword())) {
             throw new SecurityException("Account is managed by Google OAuth2. Password cannot be changed.");
        }

        if (!passwordEncoderPort.matches(request.currentPassword(), user.getPassword())) {
            throw new SecurityException("Current password is incorrect");
        }

        User updatedUser = User.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .birthDate(user.getBirthDate())
                .gender(user.getGender())
                .heightCm(user.getHeightCm())
                .password(passwordEncoderPort.encode(request.newPassword()))
                .role(user.getRole())
                .build();

        userRepositoryPort.save(updatedUser);
    }
}
