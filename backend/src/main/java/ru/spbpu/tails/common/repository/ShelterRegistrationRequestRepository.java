package ru.spbpu.tails.common.repository;

import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.spbpu.tails.common.entity.RegistrationStatus;
import ru.spbpu.tails.common.entity.ShelterRegistrationRequest;

public interface ShelterRegistrationRequestRepository
        extends JpaRepository<ShelterRegistrationRequest, Long> {

    boolean existsByEmailIgnoreCase(String email);

    @EntityGraph(attributePaths = "district")
    List<ShelterRegistrationRequest> findAllByStatusOrderByCreatedAtAsc(
            RegistrationStatus status);
}
