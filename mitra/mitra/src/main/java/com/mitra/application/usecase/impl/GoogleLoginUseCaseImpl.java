package com.mitra.application.usecase.impl;

import com.mitra.application.port.out.GoogleTokenVerifierPort;
import com.mitra.application.port.out.UserRepositoryPort;
import com.mitra.application.usecase.GoogleLoginUseCase;
import com.mitra.domain.model.User;
import com.mitra.domain.model.enums.Gender;
import com.mitra.domain.model.enums.Role;
import com.mitra.infrastructure.security.TokenService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

@Service
public class GoogleLoginUseCaseImpl implements GoogleLoginUseCase {

    private final GoogleTokenVerifierPort tokenVerifierPort;
    private final UserRepositoryPort userRepositoryPort;
    private final TokenService tokenService;

    public GoogleLoginUseCaseImpl(GoogleTokenVerifierPort tokenVerifierPort, 
                                  UserRepositoryPort userRepositoryPort, 
                                  TokenService tokenService) {
        this.tokenVerifierPort = tokenVerifierPort;
        this.userRepositoryPort = userRepositoryPort;
        this.tokenService = tokenService;
    }

    @Override
    public String execute(String idTokenString) {
        String email = tokenVerifierPort.verifyToken(idTokenString)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Google ID token"));

        Optional<User> existingUserOpt = userRepositoryPort.findByEmail(email);

        User user;
        if (existingUserOpt.isPresent()) {
            user = existingUserOpt.get();
        } else {
            User newUser = User.builder()
                    .email(email)
                    .name(email.split("@")[0])
                    .password("[OAUTH2_GOOGLE]")
                    .birthDate(LocalDate.of(2000, 1, 1))
                    .gender(Gender.MALE)
                    .heightCm(0)
                    .role(Role.USER)
                    .build();
            user = userRepositoryPort.save(newUser);
        }

        return tokenService.generateToken(user.getEmail(), user.getId());
    }
}
