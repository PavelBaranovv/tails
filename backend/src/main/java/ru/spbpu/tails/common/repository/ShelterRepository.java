package ru.spbpu.tails.common.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.spbpu.tails.common.entity.Shelter;

import java.util.Optional;

public interface ShelterRepository extends JpaRepository<Shelter, Long> {
    Optional<Shelter> findByEmail(String email);
    boolean existsByEmail(String email);
    boolean existsByEmailIgnoreCase(String email);
}
