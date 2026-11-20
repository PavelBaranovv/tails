package ru.spbpu.tails.animal.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.spbpu.tails.animal.dto.AnimalSummaryResponse;
import ru.spbpu.tails.animal.dto.AnimalResponse;
import ru.spbpu.tails.common.entity.Animal;
import ru.spbpu.tails.common.repository.AnimalRepository;

@Service
@RequiredArgsConstructor
public class AnimalService {

    private final AnimalRepository animalRepository;

    public Page<AnimalSummaryResponse> getAnimals(int page, int size) {
        if (page < 1 || size < 1) {
            throw new IllegalArgumentException("Page and size must be greater than 0");
        }

        Pageable pageable = PageRequest.of(page - 1, size);

        return animalRepository.findAll(pageable).map(this::toSummaryResponse);
    }

    public AnimalResponse getAnimalById(Long id) {
        Animal animal = animalRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("Animal with id " + id + " not found"));
        return toResponse(animal);
    }

    private AnimalSummaryResponse toSummaryResponse(Animal animal) {
        AnimalSummaryResponse response = new AnimalSummaryResponse();

        response.setId(animal.getId());
        response.setName(animal.getName());
        response.setSpecies(animal.getSpecies());
        response.setOtherSpecies(animal.getSpeciesOther());
        response.setGender(animal.getGender());
        response.setBirthMonth(animal.getBirthMonth());
        response.setBirthYear(animal.getBirthYear());
        response.setStatus(animal.getStatus());

        if (animal.getShelter() != null) {
            response.setShelterId(animal.getShelter().getId());
            response.setShelterName(animal.getShelter().getName());
            if (animal.getShelter().getDistrict() != null) {
                response.setDistrict(animal.getShelter().getDistrict().getName());
            }
        }
        //TODO photoUrl
        return response;
    }

    private AnimalResponse toResponse(Animal animal) {
        AnimalResponse response = new AnimalResponse();

        response.setId(animal.getId());
        response.setName(animal.getName());
        response.setSpecies(animal.getSpecies());
        response.setOtherSpecies(animal.getSpeciesOther());
        response.setBreed(animal.getBreed());
        response.setGender(animal.getGender());
        response.setBirthMonth(animal.getBirthMonth());
        response.setBirthYear(animal.getBirthYear());
        response.setDescription(animal.getDescription());
        response.setStatus(animal.getStatus());

        if (animal.getShelter() != null) {
            response.setShelterId(animal.getShelter().getId());
        }

        return response;
    }
}
