package ru.spbpu.tails.common.repository;

import org.jspecify.annotations.NullMarked;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ru.spbpu.tails.common.entity.Animal;

@Repository
@NullMarked
public interface AnimalRepository extends JpaRepository<Animal, Long> {
    @Override
    @EntityGraph(attributePaths = {"shelter", "shelter.district"})
    Page<Animal> findAll(Pageable pageable);
}