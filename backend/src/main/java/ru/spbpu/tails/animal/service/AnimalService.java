package ru.spbpu.tails.animal.service;

import java.util.List;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import ru.spbpu.tails.common.repository.AnimalRepository;
import ru.spbpu.tails.common.entity.Animal;

@Service
@RequiredArgsConstructor
public class AnimalService {

    private final AnimalRepository animalRepository;

    public List<Animal> getAllAnimals() {
        return animalRepository.findAll();
    }

    public Animal getAnimalById(Long id) {
        return animalRepository.findById(id).orElseThrow(() ->
                        new EntityNotFoundException("Animal with id " + id + " not found"));
    }
}

