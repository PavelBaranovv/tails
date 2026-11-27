package ru.spbpu.tails.admin.dto.response;

import ru.spbpu.tails.common.entity.RegistrationStatus;

public record ApproveRegistrationResponse(
        Long registrationRequestId,
        RegistrationStatus status
) {
}
