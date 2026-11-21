package ru.spbpu.tails.animal.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import ru.spbpu.tails.animal.dto.AnimalRequest;
import ru.spbpu.tails.animal.dto.AnimalStatusRequest;
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

    @PostMapping
    public ResponseEntity<AnimalResponse> createAnimal(
            @Valid @RequestBody AnimalRequest request,
            // TODO: Удалить shelterId после реализации авторизации
            @RequestParam Long shelterId
    ) {
        AnimalResponse response = animalService.createAnimal(request, shelterId);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{animalId}")
    public AnimalResponse updateAnimal(
            @PathVariable Long animalId,
            @Valid @RequestBody AnimalRequest request
    ) {
        return animalService.updateAnimal(animalId, request);
    }

    @DeleteMapping("/{animalId}")
    public ResponseEntity<Void> deleteAnimal(@PathVariable Long animalId) {
        animalService.deleteAnimal(animalId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{animalId}/status")
    public AnimalResponse updateAnimalStatus(
            @PathVariable Long animalId,
            @Valid @RequestBody AnimalStatusRequest request
    ) {
        return animalService.updateAnimalStatus(animalId, request);
    }
}
