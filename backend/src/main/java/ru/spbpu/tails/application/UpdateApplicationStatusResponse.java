package ru.spbpu.tails.application;

import ru.spbpu.tails.common.entity.ApplicationStatus;

public record UpdateApplicationStatusResponse(
        Long id,
        Long animalId,
        String applicantName,
        String applicantPhone,
        ApplicationStatus status
) {
}
