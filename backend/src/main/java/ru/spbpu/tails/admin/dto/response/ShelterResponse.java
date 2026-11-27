package ru.spbpu.tails.admin.dto.response;

public record ShelterResponse(
        Long id,
        String name,
        String phone,
        String email,
        String district,
        String address,
        Long availableAnimalsCount
) {
}
