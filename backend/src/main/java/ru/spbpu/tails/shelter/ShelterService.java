package ru.spbpu.tails.shelter;

import java.security.Principal;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.spbpu.tails.common.entity.Shelter;
import ru.spbpu.tails.common.repository.ShelterRepository;

@Service
public class ShelterService {

    private final ShelterRepository shelterRepository;

    public ShelterService(ShelterRepository shelterRepository) {
        this.shelterRepository = shelterRepository;
    }

    @Transactional(readOnly = true)
    public ShelterProfile getProfile(Principal principal) {
        if (principal == null || principal.getName() == null || principal.getName().isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Authentication is required");
        }

        Shelter shelter = shelterRepository.findByEmailIgnoreCase(principal.getName())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.FORBIDDEN,
                        "Authenticated account is not a shelter"));

        return new ShelterProfile(
                shelter.getName(),
                shelter.getDistrict().getName(),
                shelter.getAddress(),
                shelter.getPhone());
    }
}
