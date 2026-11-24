package ru.spbpu.tails.auth.dto;

import ru.spbpu.tails.common.entity.RegistrationStatus;

public record RegisterShelterResponse(
        Long registrationRequestId,
        RegistrationStatus status
) {
}
