package ru.spbpu.tails.animal.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.spbpu.tails.common.entity.AnimalStatus;
import ru.spbpu.tails.common.entity.Gender;
import ru.spbpu.tails.common.entity.Species;

@Getter
@Setter
@NoArgsConstructor
public class AnimalSummaryResponse {

    private Long id;

    private String name;

    private Species species;

    private String otherSpecies;

    private Gender gender;

    private Short birthMonth;

    private Short birthYear;

    private AnimalStatus status;

    private Long shelterId;

    private String shelterName;

    private String district;

    private String photoUrl;
}