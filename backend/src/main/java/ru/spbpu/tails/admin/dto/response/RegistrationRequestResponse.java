package ru.spbpu.tails.admin.dto.response;

public record RegistrationRequestResponse(
        Long id,
        String shelterName,
        String district,
        String address,
        String contactName,
        String phone,
        String email
) {
}
