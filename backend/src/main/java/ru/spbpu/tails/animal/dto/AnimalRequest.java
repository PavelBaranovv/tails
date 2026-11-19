
package ru.spbpu.tails.animal.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import ru.spbpu.tails.common.entity.Gender;
import ru.spbpu.tails.common.entity.Species;

@Getter
@Setter
@NoArgsConstructor
public class AnimalRequest {

    @NotBlank
    @Size(min = 2, max = 64)
    private String name;

    @NotNull
    private Species species;

    @Size(max = 64)
    private String otherSpecies;

    @Size(max = 64)
    private String breed;

    private Gender sex;

    @Min(1)
    @Max(12)
    private Short birthMonth;

    private Short birthYear;

    @Size(min = 8, max = 1024)
    private String description;
}
