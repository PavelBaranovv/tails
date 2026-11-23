package ru.spbpu.tails.application.dto.response;

import ru.spbpu.tails.common.entity.ApplicationStatus;

public record UpdateApplicationStatusResponse(
        Long id,
        Long animalId,
        String applicantName,
        String applicantPhone,
        ApplicationStatus status
) {
}
