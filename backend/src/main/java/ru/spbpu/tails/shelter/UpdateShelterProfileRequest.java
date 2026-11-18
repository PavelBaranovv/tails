package ru.spbpu.tails.shelter;

public record UpdateShelterProfileRequest(
        String name,
        String district,
        String address,
        String phone
) {
}
