package ru.spbpu.tails.application.dto.request;

import ru.spbpu.tails.common.entity.ApplicationStatus;

public record UpdateApplicationStatusRequest(ApplicationStatus status) {
}
