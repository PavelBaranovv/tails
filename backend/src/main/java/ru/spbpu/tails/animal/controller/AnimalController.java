package ru.spbpu.tails.animal.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import ru.spbpu.tails.animal.dto.AnimalSummaryResponse;
import ru.spbpu.tails.animal.dto.AnimalResponse;
import ru.spbpu.tails.animal.service.AnimalService;
import ru.spbpu.tails.common.entity.AnimalStatus;
import ru.spbpu.tails.common.entity.Species;

@RestController
@RequestMapping("/api/v1/animals")
@RequiredArgsConstructor
public class AnimalController {

    private final AnimalService animalService;

    @GetMapping
    public Page<AnimalSummaryResponse> getAnimals(
            @RequestParam(required = false) String district,
            @RequestParam(required = false) Species species,
            @RequestParam(required = false) Integer minAge,
            @RequestParam(required = false) Integer maxAge,
            @RequestParam(required = false) AnimalStatus status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return animalService.getAnimals(district, species, minAge, maxAge, status, page, size);
    }

    @GetMapping("/{animalId}")
    public AnimalResponse getAnimalById(@PathVariable Long animalId) {
        return animalService.getAnimalById(animalId);
    }
}
