package ru.spbpu.tails.common.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.spbpu.tails.common.entity.ShelterRegistrationRequest;

public interface ShelterRegistrationRequestRepository
        extends JpaRepository<ShelterRegistrationRequest, Long> {

    boolean existsByEmailIgnoreCase(String email);
}
