package ru.spbpu.tails.common.repository;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.spbpu.tails.common.entity.Animal;
import ru.spbpu.tails.common.entity.AnimalStatus;
import ru.spbpu.tails.common.entity.Species;

import java.util.List;
import java.util.Optional;

@Repository
@NullMarked
public interface AnimalRepository extends JpaRepository<Animal, Long> {
    
    @Override
    @EntityGraph(attributePaths = {"shelter", "shelter.district"})
    Optional<Animal> findById(Long id);

    @EntityGraph(attributePaths = {"shelter", "shelter.district"})
    @Query("""
            SELECT a FROM Animal a
            WHERE (:district IS NULL OR LOWER(a.shelter.district.name) = LOWER(:district))
            AND (:species IS NULL OR a.species = :species)
            AND (:status IS NULL OR a.status = :status)
            AND (:minAge IS NULL OR ((YEAR(CURRENT_DATE) - a.birthYear) * 12 + MONTH(CURRENT_DATE) - a.birthMonth) >= :minAge)
            AND (:maxAge IS NULL OR ((YEAR(CURRENT_DATE) - a.birthYear) * 12 + MONTH(CURRENT_DATE) - a.birthMonth) <= :maxAge)
            """)
    Page<Animal> findAllWithFilters(
            @Param("district") @Nullable String district,
            @Param("species") @Nullable Species species,
            @Param("status") @Nullable AnimalStatus status,
            @Param("minAge") @Nullable Integer minAge,
            @Param("maxAge") @Nullable Integer maxAge,
            Pageable pageable
    );

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