package ru.spbpu.tails.animal.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ShelterResponse {
    private Long id;

    private String name;

    private String phone;

    private String district;

    private String address;
}
