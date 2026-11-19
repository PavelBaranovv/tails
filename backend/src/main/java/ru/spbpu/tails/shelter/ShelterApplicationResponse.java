package ru.spbpu.tails.shelter;

import ru.spbpu.tails.common.entity.ApplicationStatus;

public record ShelterApplicationResponse(
        Long id,
        ApplicationStatus status,
        String applicantName,
        String applicantPhone,
        Long animalId,
        String animalName
) {
}
