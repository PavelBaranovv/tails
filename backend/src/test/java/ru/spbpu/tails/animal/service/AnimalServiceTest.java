package ru.spbpu.tails.animal.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import ru.spbpu.tails.animal.dto.AnimalRequest;
import ru.spbpu.tails.animal.dto.AnimalResponse;
import ru.spbpu.tails.animal.dto.AnimalStatusRequest;
import ru.spbpu.tails.animal.dto.AnimalSummaryResponse;
import ru.spbpu.tails.common.entity.Animal;
import ru.spbpu.tails.common.entity.AnimalStatus;
import ru.spbpu.tails.common.entity.Gender;
import ru.spbpu.tails.common.entity.Shelter;
import ru.spbpu.tails.common.entity.Species;
import ru.spbpu.tails.common.repository.AnimalRepository;
import ru.spbpu.tails.common.repository.ShelterRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AnimalServiceTest {

    @Mock
    private AnimalRepository animalRepository;

    @Mock
    private ShelterRepository shelterRepository;

    @InjectMocks
    private AnimalService animalService;

    private Animal animal;
    private Shelter shelter;

    @BeforeEach
    void setUp() {
        shelter = new Shelter();
        shelter.setId(1L);
        shelter.setName("Приют Добрые лапы");

        animal = new Animal();
        animal.setId(1L);
        animal.setShelter(shelter);
        animal.setName("Барсик");
        animal.setSpecies(Species.CAT);
        animal.setBreed("Британская");
        animal.setGender(Gender.MALE);
        animal.setBirthMonth((short) 5);
        animal.setBirthYear((short) 2024);
        animal.setDescription("Спокойный и дружелюбный кот.");
        animal.setStatus(AnimalStatus.AVAILABLE);
    }

    @Test
    void getAnimals_shouldReturnAnimals() {
        Page<Animal> animalPage = new PageImpl<>(List.of(animal));

        when(animalRepository.findAllWithFilters(isNull(), isNull(), isNull(), isNull(), isNull(), any(PageRequest.class)))
                .thenReturn(animalPage);

        Page<AnimalSummaryResponse> result = animalService.getAnimals(
                        null, null, null, null, null, 1, 10);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("Барсик", result.getContent().getFirst().getName());

        verify(animalRepository).findAllWithFilters(isNull(), isNull(), isNull(), isNull(), isNull(), any(PageRequest.class));
    }

    @Test
    void getAnimalById_shouldReturnAnimal() {
        when(animalRepository.findById(1L)).thenReturn(Optional.of(animal));

        AnimalResponse result = animalService.getAnimalById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Барсик", result.getName());
        assertEquals(Species.CAT, result.getSpecies());
        assertEquals(AnimalStatus.AVAILABLE, result.getStatus());

        verify(animalRepository).findById(1L);
    }

    @Test
    void getAnimalById_shouldThrowExceptionWhenAnimalNotFound() {
        when(animalRepository.findById(100L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> animalService.getAnimalById(100L));

        verify(animalRepository).findById(100L);
    }

    @Test
    void createAnimal_shouldSaveAndReturnAnimal() {
        AnimalRequest request = new AnimalRequest();
        request.setName("Барсик");
        request.setSpecies(Species.CAT);
        request.setBreed("Британская");
        request.setGender(Gender.MALE);
        request.setBirthMonth((short) 5);
        request.setBirthYear((short) 2024);
        request.setDescription("Спокойный и дружелюбный кот.");

        when(shelterRepository.findById(1L)).thenReturn(Optional.of(shelter));

        when(animalRepository.save(any(Animal.class))).thenAnswer(invocation -> {
                    Animal saved = invocation.getArgument(0);
                    saved.setId(1L);
                    return saved;
                });

        AnimalResponse result = animalService.createAnimal(request, 1L);

        assertNotNull(result);
        assertEquals("Барсик", result.getName());
        assertEquals(Species.CAT, result.getSpecies());
        assertEquals(AnimalStatus.AVAILABLE, result.getStatus());

        verify(shelterRepository).findById(1L);
        verify(animalRepository).save(any(Animal.class));
    }

    @Test
    void createAnimal_shouldThrowExceptionWhenShelterNotFound() {
        AnimalRequest request = new AnimalRequest();
        request.setName("Барсик");
        request.setSpecies(Species.CAT);

        when(shelterRepository.findById(100L))
                .thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> animalService.createAnimal(request, 100L));

        verify(animalRepository, never()).save(any(Animal.class));
    }

    @Test
    void updateAnimal_shouldUpdateAndReturnAnimal() {
        AnimalRequest request = new AnimalRequest();
        request.setName("Мурзик");
        request.setSpecies(Species.CAT);
        request.setBreed("Дворовый");
        request.setGender(Gender.MALE);
        request.setBirthMonth((short) 3);
        request.setBirthYear((short) 2023);
        request.setDescription("Активный и добрый кот.");

        when(animalRepository.findById(1L)).thenReturn(Optional.of(animal));
        when(animalRepository.save(any(Animal.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        AnimalResponse result = animalService.updateAnimal(1L, request);

        assertNotNull(result);
        assertEquals("Мурзик", result.getName());
        assertEquals("Дворовый", result.getBreed());
        assertEquals((short) 2023, result.getBirthYear());

        verify(animalRepository).save(animal);
    }

    @Test
    void updateAnimal_shouldThrowExceptionWhenAnimalNotFound() {
        AnimalRequest request = new AnimalRequest();

        when(animalRepository.findById(100L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> animalService.updateAnimal(100L, request));

        verify(animalRepository, never()).save(any(Animal.class));
    }

    @Test
    void deleteAnimal_shouldDeleteAnimal() {
        when(animalRepository.findById(1L)).thenReturn(Optional.of(animal));

        animalService.deleteAnimal(1L);

        verify(animalRepository).findById(1L);
        verify(animalRepository).delete(animal);
    }

    @Test
    void deleteAnimal_shouldThrowExceptionWhenAnimalNotFound() {
        when(animalRepository.findById(100L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> animalService.deleteAnimal(100L));

        verify(animalRepository, never()).delete(any(Animal.class));
    }

    @Test
    void updateAnimalStatus_shouldChangeStatus() {
        AnimalStatusRequest request = new AnimalStatusRequest();
        request.setStatus(AnimalStatus.ADOPTED);

        when(animalRepository.findById(1L)).thenReturn(Optional.of(animal));
        when(animalRepository.save(any(Animal.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        AnimalResponse result = animalService.updateAnimalStatus(1L, request);

        assertNotNull(result);
        assertEquals(AnimalStatus.ADOPTED, result.getStatus());
        assertEquals("Барсик", result.getName());
        assertEquals(Species.CAT, result.getSpecies());

        verify(animalRepository).save(animal);
    }

    @Test
    void updateAnimalStatus_shouldThrowExceptionWhenAnimalNotFound() {
        AnimalStatusRequest request = new AnimalStatusRequest();
        request.setStatus(AnimalStatus.ADOPTED);

        when(animalRepository.findById(100L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> animalService.updateAnimalStatus(100L, request));

        verify(animalRepository, never()).save(any(Animal.class));
    }
}
