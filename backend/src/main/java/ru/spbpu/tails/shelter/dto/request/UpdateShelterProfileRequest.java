package ru.spbpu.tails.shelter.dto.request;

public record UpdateShelterProfileRequest(
        String name,
        String district,
        String address,
        String phone
) {
}
