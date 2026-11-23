package ru.spbpu.tails.application.dto.request;

public record CreateApplicationRequest(
        Long animalId,
        String applicantName,
        String applicantPhone
) {
}
