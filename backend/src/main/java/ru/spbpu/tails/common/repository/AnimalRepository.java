package ru.spbpu.tails.common.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ru.spbpu.tails.common.entity.Animal;

@Repository
public interface AnimalRepository extends JpaRepository<Animal, Long> {

}