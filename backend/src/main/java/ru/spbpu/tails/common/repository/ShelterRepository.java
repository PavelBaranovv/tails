package ru.spbpu.tails.common.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.spbpu.tails.common.entity.Shelter;

public interface ShelterRepository extends JpaRepository<Shelter, Long> {
}