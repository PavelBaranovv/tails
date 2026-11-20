package ru.spbpu.tails.application;

import ru.spbpu.tails.common.entity.ApplicationStatus;

public record CreateApplicationResponse(
        Long id,
        ApplicationStatus status
) {
}
