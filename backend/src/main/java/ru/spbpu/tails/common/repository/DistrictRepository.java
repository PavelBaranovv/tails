package ru.spbpu.tails.common.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.spbpu.tails.common.entity.District;

public interface DistrictRepository extends JpaRepository<District, Short> {

    Optional<District> findByNameIgnoreCase(String name);
}
