package ru.spbpu.tails.common.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.spbpu.tails.common.entity.AdoptionApplication;
import ru.spbpu.tails.common.entity.ApplicationStatus;

public interface ApplicationRepository extends JpaRepository<AdoptionApplication, Long> {

    @Query("""
            SELECT application FROM AdoptionApplication application
            JOIN FETCH application.animal
            WHERE application.shelter.id = :shelterId
            ORDER BY application.createdAt DESC
            """)
    List<AdoptionApplication> findShelterApplications(
            @Param("shelterId") Long shelterId);

    @Query("""
            SELECT application FROM AdoptionApplication application
            JOIN FETCH application.animal
            WHERE application.shelter.id = :shelterId
              AND application.status = :status
            ORDER BY application.createdAt DESC
            """)
    List<AdoptionApplication> findShelterApplicationsByStatus(
            @Param("shelterId") Long shelterId,
            @Param("status") ApplicationStatus status);
}
