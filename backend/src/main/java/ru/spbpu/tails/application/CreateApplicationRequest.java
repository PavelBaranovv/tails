package ru.spbpu.tails.application;

public record CreateApplicationRequest(
        Long animalId,
        String applicantName,
        String applicantPhone
) {
}
