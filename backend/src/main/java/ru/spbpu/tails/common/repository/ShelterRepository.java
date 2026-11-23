package ru.spbpu.tails.common.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.spbpu.tails.common.entity.Shelter;

public interface ShelterRepository extends JpaRepository<Shelter, Long> {

    Optional<Shelter> findByEmailIgnoreCase(String email);
    Optional<Shelter> findByEmail(String email);
    boolean existsByEmail(String email);
}
