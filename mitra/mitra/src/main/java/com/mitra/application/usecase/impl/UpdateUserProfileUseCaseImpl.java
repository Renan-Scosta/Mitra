package com.mitra.application.usecase.impl;

import com.mitra.application.exception.ResourceNotFoundException;
import com.mitra.application.port.out.UserRepositoryPort;
import com.mitra.application.usecase.UpdateUserProfileUseCase;
import com.mitra.domain.model.User;
import com.mitra.presentation.dto.request.UpdateUserProfileRequestDto;
import com.mitra.presentation.dto.response.UserProfileResponseDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UpdateUserProfileUseCaseImpl implements UpdateUserProfileUseCase {

    private final UserRepositoryPort userRepositoryPort;

    public UpdateUserProfileUseCaseImpl(UserRepositoryPort userRepositoryPort) {
        this.userRepositoryPort = userRepositoryPort;
    }

    @Override
    public UserProfileResponseDto execute(Long userId, UpdateUserProfileRequestDto request) {
        User user = userRepositoryPort.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));

        User updatedUser = User.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(request.name())
                .birthDate(request.birthDate())
                .gender(request.gender())
                .heightCm(request.heightCm())
                .password(user.getPassword())
                .role(user.getRole())
                .build();

        User savedUser = userRepositoryPort.save(updatedUser);

        return new UserProfileResponseDto(
                savedUser.getId(),
                savedUser.getEmail(),
                savedUser.getName(),
                savedUser.getBirthDate(),
                savedUser.getGender(),
                savedUser.getHeightCm(),
                savedUser.getAge()
        );
    }
}
