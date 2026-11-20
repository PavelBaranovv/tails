package ru.spbpu.tails.shelter;

import ru.spbpu.tails.common.entity.AnimalStatus;
import ru.spbpu.tails.common.entity.Gender;
import ru.spbpu.tails.common.entity.Species;

public record ShelterPetResponse(
        Long id,
        String photoKey,
        Species species,
        String speciesOther,
        String name,
        String age,
        Short birthMonth,
        Short birthYear,
        Gender gender,
        String breed,
        AnimalStatus status
) {
}
