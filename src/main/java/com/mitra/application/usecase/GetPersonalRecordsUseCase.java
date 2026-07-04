package com.mitra.application.usecase;

import com.mitra.presentation.dto.response.PersonalRecordResponseDto;

public interface GetPersonalRecordsUseCase {
    PersonalRecordResponseDto execute(Long userId, Long exerciseId);
}
