package ru.spbpu.tails.application.dto.response;

import ru.spbpu.tails.common.entity.ApplicationStatus;

public record ApplicationResponse(
        Long id,
        ApplicationStatus status,
        String applicantName,
        String applicantPhone,
        Long animalId,
        String animalName
) {
}
