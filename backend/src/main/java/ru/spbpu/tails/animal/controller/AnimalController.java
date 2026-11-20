package ru.spbpu.tails.animal.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import ru.spbpu.tails.animal.dto.AnimalSummaryResponse;
import ru.spbpu.tails.animal.dto.AnimalResponse;
import ru.spbpu.tails.animal.service.AnimalService;

@RestController
@RequestMapping("/api/v1/animals")
@RequiredArgsConstructor
public class AnimalController {

    private final AnimalService animalService;

    @GetMapping
    public Page<AnimalSummaryResponse> getAnimals(@RequestParam(defaultValue = "1") int page,
                                                  @RequestParam(defaultValue = "10") int size) {
        return animalService.getAnimals(page, size);
    }

    @GetMapping("/{animalId}")
    public AnimalResponse getAnimalById(@PathVariable Long animalId) {
        return animalService.getAnimalById(animalId);
    }
}
