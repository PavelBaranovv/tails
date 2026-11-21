package ru.spbpu.tails.animal.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import ru.spbpu.tails.common.entity.Gender;
import ru.spbpu.tails.common.entity.Species;

import java.time.Year;

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

    @Size(min = 2, max = 64)
    private String breed;

    private Gender gender;

    @Min(1)
    @Max(12)
    private Short birthMonth;

    private Short birthYear;

    @Size(min = 8, max = 1024)
    private String description;

    @AssertTrue(message = "Birth month and year must be specified together")
    public boolean isBirthDateComplete() {
        return (birthMonth == null && birthYear == null)
                || (birthMonth != null && birthYear != null);
    }

    @AssertTrue(message = "Birth year must be between current year minus 200 and current year")
    public boolean isBirthYearValid() {
        if (birthYear == null) {
            return true;
        }

        int currentYear = Year.now().getValue();

        return birthYear >= currentYear - 200
                && birthYear <= currentYear;
    }

    @AssertTrue(message = "Other species must be specified when species is OTHER")
    public boolean isOtherSpeciesValid() {
        if (species == Species.OTHER) {
            return otherSpecies != null && !otherSpecies.isBlank();
        }

        return true;
    }
}
