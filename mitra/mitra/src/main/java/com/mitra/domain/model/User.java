package com.mitra.domain.model;

import com.mitra.domain.model.enums.Gender;
import com.mitra.domain.model.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.Period;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    private Long id;
    private String email;
    private String name;
    private LocalDate birthDate;
    private Gender gender;
    private int heightCm;
    private String password;
    
    @Builder.Default
    private Role role = Role.USER;

    public int getAge() {
        return Period.between(birthDate, LocalDate.now()).getYears();
    }
}
