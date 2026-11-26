package ru.spbpu.tails.common.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;
import ru.spbpu.tails.common.entity.RegistrationStatus;
import ru.spbpu.tails.common.entity.ShelterRegistrationRequest;

public interface ShelterRegistrationRequestRepository
        extends JpaRepository<ShelterRegistrationRequest, Long> {

    boolean existsByEmailIgnoreCase(String email);

    @EntityGraph(attributePaths = "district")
    List<ShelterRegistrationRequest> findAllByStatusOrderByCreatedAtAsc(
            RegistrationStatus status);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            SELECT request FROM ShelterRegistrationRequest request
            JOIN FETCH request.district
            WHERE request.id = :id
            """)
    Optional<ShelterRegistrationRequest> findByIdForUpdate(@Param("id") Long id);
}
