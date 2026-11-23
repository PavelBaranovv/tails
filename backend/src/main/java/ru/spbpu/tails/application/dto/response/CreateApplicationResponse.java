package ru.spbpu.tails.application.dto.response;

import ru.spbpu.tails.common.entity.ApplicationStatus;

public record CreateApplicationResponse(
        Long id,
        ApplicationStatus status
) {
}
