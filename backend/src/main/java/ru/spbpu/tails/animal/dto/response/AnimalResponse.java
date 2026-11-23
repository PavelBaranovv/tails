package ru.spbpu.tails.animal.dto.response;

import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.spbpu.tails.common.entity.AnimalStatus;
import ru.spbpu.tails.common.entity.Gender;
import ru.spbpu.tails.common.entity.Species;

@Getter
@Setter
@NoArgsConstructor
public class AnimalResponse {

    private Long id;

    private String name;

    private Species species;

    private String otherSpecies;

    private Gender gender;

    private Short birthMonth;

    private Short birthYear;

    private String breed;

    private String description;

    private AnimalStatus status;

    private List<AnimalPhotoResponse> photos;

    private ShelterResponse shelter;
}
