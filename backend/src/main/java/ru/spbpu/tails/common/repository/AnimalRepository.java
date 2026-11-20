package ru.spbpu.tails.common.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.spbpu.tails.common.entity.Animal;
import ru.spbpu.tails.common.entity.AnimalStatus;
import ru.spbpu.tails.common.entity.Species;

public interface AnimalRepository extends JpaRepository<Animal, Long> {

    @Query("""
            SELECT DISTINCT animal FROM Animal animal
            LEFT JOIN FETCH animal.photos
            WHERE animal.shelter.id = :shelterId AND animal.status = :status
            ORDER BY animal.createdAt DESC
            """)
    List<Animal> findShelterPetsByStatus(
            @Param("shelterId") Long shelterId,
            @Param("status") AnimalStatus status);

    @Query("""
            SELECT DISTINCT animal FROM Animal animal
            LEFT JOIN FETCH animal.photos
            WHERE animal.shelter.id = :shelterId
              AND animal.status = :status
              AND animal.species = :species
            ORDER BY animal.createdAt DESC
            """)
    List<Animal> findShelterPetsByStatusAndSpecies(
            @Param("shelterId") Long shelterId,
            @Param("status") AnimalStatus status,
            @Param("species") Species species);
}
