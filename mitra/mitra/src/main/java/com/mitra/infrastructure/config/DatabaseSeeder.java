package com.mitra.infrastructure.config;

import com.mitra.domain.model.enums.Gender;
import com.mitra.infrastructure.persistence.entity.UserEntity;
import com.mitra.infrastructure.persistence.repository.UserJpaRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@Profile("!test")
public class DatabaseSeeder implements CommandLineRunner {

    private final UserJpaRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DatabaseSeeder(UserJpaRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.findByEmail("dev@mitra.com").isEmpty()) {
            UserEntity devUser = UserEntity.builder()
                    .name("Dev Mitra")
                    .email("dev@mitra.com")
                    .password(passwordEncoder.encode("123456"))
                    .birthDate(LocalDate.of(1990, 1, 1))
                    .gender(Gender.MALE)
                    .heightCm(180)
                    .build();
            
            userRepository.save(devUser);
        }
    }
}
