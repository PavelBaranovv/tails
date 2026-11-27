package ru.spbpu.tails.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterShelterRequest(
        @NotBlank
        @Size(min = 2, max = 256)
        @Pattern(regexp = "^[\\p{L}\\d «»\"—.,!:/\\\\@|#()*%$^-]+$")
        String name,

        @NotBlank
        @Size(max = 128)
        String district,

        @NotBlank
        @Size(min = 2, max = 256)
        @Pattern(regexp = "^[\\p{L}\\d ,./№-]+$")
        String address,

        @NotBlank
        @Size(min = 2, max = 128)
        @Pattern(regexp = "^[\\p{L} -]+$")
        String contactName,

        @NotBlank
        @Size(min = 7, max = 18)
        @Pattern(regexp = "^\\+?\\d+$")
        String phone,

        @NotBlank
        @Email
        @Size(min = 5, max = 254)
        String email,

        @NotBlank
        @Size(min = 8, max = 64)
        @Pattern(regexp = "^[A-Za-z0-9!\"#$%&()*+,./:;<=>?@\\[\\]\\\\^_-]+$")
        String password
) {
}
