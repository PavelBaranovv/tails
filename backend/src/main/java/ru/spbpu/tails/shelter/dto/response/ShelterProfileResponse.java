package ru.spbpu.tails.shelter.dto.response;

public record ShelterProfileResponse(
        String name,
        String district,
        String address,
        String phone
) {
}
