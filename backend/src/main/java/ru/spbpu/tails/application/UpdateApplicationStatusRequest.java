package ru.spbpu.tails.application;

import ru.spbpu.tails.common.entity.ApplicationStatus;

public record UpdateApplicationStatusRequest(ApplicationStatus status) {
}
