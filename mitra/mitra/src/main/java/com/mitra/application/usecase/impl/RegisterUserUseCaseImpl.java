package com.mitra.application.usecase.impl;

import com.mitra.application.port.out.BodyMeasurementRepositoryPort;
import com.mitra.application.port.out.PasswordEncoderPort;
import com.mitra.application.port.out.UserRepositoryPort;
import com.mitra.application.usecase.RegisterUserUseCase;
import com.mitra.domain.model.BodyMeasurement;
import com.mitra.domain.model.User;
import com.mitra.domain.model.enums.Role;
import com.mitra.presentation.dto.request.CreateUserRequestDto;

import java.time.LocalDate;

public class RegisterUserUseCaseImpl implements RegisterUserUseCase {

    private final UserRepositoryPort userRepository;
    private final BodyMeasurementRepositoryPort bodyMeasurementRepository;
    private final PasswordEncoderPort passwordEncoderPort;

    public RegisterUserUseCaseImpl(
            UserRepositoryPort userRepository,
            BodyMeasurementRepositoryPort bodyMeasurementRepository,
            PasswordEncoderPort passwordEncoderPort) {
        this.userRepository = userRepository;
        this.bodyMeasurementRepository = bodyMeasurementRepository;
        this.passwordEncoderPort = passwordEncoderPort;
    }

    @Override
    public Long execute(CreateUserRequestDto request) {
        userRepository.findByEmail(request.email()).ifPresent(existing -> {
            throw new IllegalStateException("Email already registered: " + request.email());
        });

        User user = User.builder()
                .name(request.name())
                .email(request.email())
                .password(passwordEncoderPort.encode(request.password()))
                .birthDate(request.birthDate())
                .gender(request.gender())
                .heightCm(request.heightCm())
                .role(Role.USER)
                .build();

        User savedUser = userRepository.save(user);

        BodyMeasurement initialMeasurement = BodyMeasurement.builder()
                .userId(savedUser.getId())
                .weightKg(request.initialWeightKg())
                .recordDate(LocalDate.now())
                .build();

        bodyMeasurementRepository.save(initialMeasurement);

        return savedUser.getId();
    }
}
