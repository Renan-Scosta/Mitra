package com.mitra.infrastructure.persistence.mapper;

import com.mitra.domain.model.User;
import com.mitra.infrastructure.persistence.entity.UserEntity;

public final class UserMapper {

    private UserMapper() {}

    public static User toDomain(UserEntity entity) {
        return User.builder()
                .id(entity.getId())
                .email(entity.getEmail())
                .name(entity.getName())
                .password(entity.getPassword())
                .birthDate(entity.getBirthDate())
                .gender(entity.getGender())
                .heightCm(entity.getHeightCm())
                .build();
    }

    public static UserEntity toEntity(User domain) {
        return UserEntity.builder()
                .id(domain.getId())
                .email(domain.getEmail())
                .name(domain.getName())
                .password(domain.getPassword())
                .birthDate(domain.getBirthDate())
                .gender(domain.getGender())
                .heightCm(domain.getHeightCm())
                .build();
    }
}
