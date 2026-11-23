package ru.spbpu.tails.common.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.spbpu.tails.common.entity.AnimalPhoto;

@Repository
public interface AnimalPhotoRepository extends JpaRepository<AnimalPhoto, Long> {
}
