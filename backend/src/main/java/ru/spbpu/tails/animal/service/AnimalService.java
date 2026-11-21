package ru.spbpu.tails.animal.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.spbpu.tails.animal.dto.AnimalRequest;
import ru.spbpu.tails.animal.dto.AnimalSummaryResponse;
import ru.spbpu.tails.animal.dto.AnimalResponse;
import ru.spbpu.tails.animal.dto.ShelterResponse;
import ru.spbpu.tails.common.entity.Animal;
import ru.spbpu.tails.common.entity.AnimalStatus;
import ru.spbpu.tails.common.entity.Shelter;
import ru.spbpu.tails.common.entity.Species;
import ru.spbpu.tails.common.repository.AnimalRepository;
import ru.spbpu.tails.common.repository.ShelterRepository;

@Service
@RequiredArgsConstructor
public class AnimalService {

    private final AnimalRepository animalRepository;
    private final ShelterRepository shelterRepository;

    public Page<AnimalSummaryResponse> getAnimals(
            @Nullable String district,
            @Nullable Species species,
            @Nullable Integer minAge,
            @Nullable Integer maxAge,
            @Nullable AnimalStatus status,
            int page,
            int size
    ) {
        if (page < 1 || size < 1) {
            throw new IllegalArgumentException("Page and size must be greater than 0");
        }

        if (district != null && district.isBlank()) {
            district = null;
        } else if (district != null) {
            district = district.trim();
        }

        Pageable pageable = PageRequest.of(page - 1, size);

        return animalRepository.findAllWithFilters(district, species, status, minAge, maxAge, pageable)
                .map(this::toSummaryResponse);
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
        response.setGender(animal.getGender());
        response.setBirthMonth(animal.getBirthMonth());
        response.setBirthYear(animal.getBirthYear());
        response.setBreed(animal.getBreed());
        response.setDescription(animal.getDescription());
        response.setStatus(animal.getStatus());

        if (animal.getShelter() != null) {
            ShelterResponse shelterResponse = new ShelterResponse();
            shelterResponse.setId(animal.getShelter().getId());
            shelterResponse.setName(animal.getShelter().getName());
            shelterResponse.setPhone(animal.getShelter().getPhone());
            shelterResponse.setAddress(animal.getShelter().getAddress());

            if (animal.getShelter().getDistrict() != null) {
                shelterResponse.setDistrict(animal.getShelter().getDistrict().getName());
            }

            response.setShelter(shelterResponse);
        }

        //TODO photos
        response.setPhotos(java.util.Collections.emptyList());

        return response;
    }



    @Transactional
    public AnimalResponse createAnimal(AnimalRequest request, Long shelterId) {

        // TODO: После реализации авторизации получать приют из данных текущего авторизованного пользователя.
        Shelter shelter = shelterRepository.findById(shelterId)
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "Shelter with id " + shelterId + " not found"
                        )
                );

        Animal animal = new Animal();

        animal.setShelter(shelter);
        animal.setName(request.getName());
        animal.setSpecies(request.getSpecies());
        animal.setSpeciesOther(request.getOtherSpecies());
        animal.setBreed(request.getBreed());
        animal.setGender(request.getGender());
        animal.setBirthMonth(request.getBirthMonth());
        animal.setBirthYear(request.getBirthYear());
        animal.setDescription(request.getDescription());
        animal.setStatus(AnimalStatus.AVAILABLE);

        Animal savedAnimal = animalRepository.save(animal);

        return toResponse(savedAnimal);
    }
}
