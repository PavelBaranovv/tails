package ru.spbpu.tails.admin.repository;

import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;
import ru.spbpu.tails.admin.dto.response.ShelterResponse;
import ru.spbpu.tails.common.entity.AnimalStatus;
import ru.spbpu.tails.common.entity.Shelter;

public interface AdminShelterRepository extends Repository<Shelter, Long> {

    @Query("""
            SELECT new ru.spbpu.tails.admin.dto.response.ShelterResponse(
                shelter.id,
                shelter.name,
                shelter.phone,
                shelter.email,
                shelter.district.name,
                shelter.address,
                COUNT(animal.id)
            )
            FROM Shelter shelter
            LEFT JOIN Animal animal
                ON animal.shelter = shelter AND animal.status = :status
            GROUP BY
                shelter.id,
                shelter.name,
                shelter.phone,
                shelter.email,
                shelter.district.name,
                shelter.address
            ORDER BY shelter.name ASC
            """)
    List<ShelterResponse> findAllWithAnimalsCountByStatus(
            @Param("status") AnimalStatus status);
}
