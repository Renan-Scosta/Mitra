package com.mitra.application.usecase.impl;

import com.mitra.application.exception.ResourceNotFoundException;
import com.mitra.application.port.out.UserRepositoryPort;
import com.mitra.application.usecase.DeleteUserAccountUseCase;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class DeleteUserAccountUseCaseImpl implements DeleteUserAccountUseCase {

    private final UserRepositoryPort userRepositoryPort;

    public DeleteUserAccountUseCaseImpl(UserRepositoryPort userRepositoryPort) {
        this.userRepositoryPort = userRepositoryPort;
    }

    @Override
    public void execute(Long userId) {
        userRepositoryPort.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));

        userRepositoryPort.deleteById(userId);
    }
}
