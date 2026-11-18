package ru.spbpu.tails.shelter;

import java.security.Principal;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.spbpu.tails.common.entity.District;
import ru.spbpu.tails.common.entity.Shelter;
import ru.spbpu.tails.common.repository.DistrictRepository;
import ru.spbpu.tails.common.repository.ShelterRepository;

@Service
public class ShelterService {

    private final ShelterRepository shelterRepository;
    private final DistrictRepository districtRepository;

    public ShelterService(
            ShelterRepository shelterRepository,
            DistrictRepository districtRepository) {
        this.shelterRepository = shelterRepository;
        this.districtRepository = districtRepository;
    }

    @Transactional(readOnly = true)
    public ShelterProfile getProfile(Principal principal) {
        return toProfile(getShelter(principal));
    }

    @Transactional
    public ShelterProfile updateProfile(
            Principal principal,
            UpdateShelterProfileRequest request) {
        if (request == null) {
            throw badRequest("Profile data is required");
        }

        String name = validateLength(request.name(), "name", 2, 256);
        String districtName = validateLength(request.district(), "district", 1, 128);
        String address = validateLength(request.address(), "address", 2, 256);
        String phone = validateLength(request.phone(), "phone", 7, 18);

        Shelter shelter = getShelter(principal);
        District district = districtRepository.findByNameIgnoreCase(districtName)
                .orElseThrow(() -> badRequest("Unknown district: " + districtName));

        shelter.setName(name);
        shelter.setDistrict(district);
        shelter.setAddress(address);
        shelter.setPhone(phone);

        return toProfile(shelter);
    }

    private Shelter getShelter(Principal principal) {
        if (principal == null || principal.getName() == null || principal.getName().isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Authentication is required");
        }

        return shelterRepository.findByEmailIgnoreCase(principal.getName())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.FORBIDDEN,
                        "Authenticated account is not a shelter"));
    }

    private ShelterProfile toProfile(Shelter shelter) {
        return new ShelterProfile(
                shelter.getName(),
                shelter.getDistrict().getName(),
                shelter.getAddress(),
                shelter.getPhone());
    }

    private String validateLength(String value, String field, int min, int max) {
        if (value == null) {
            throw badRequest(field + " is required");
        }

        String trimmedValue = value.trim();
        if (trimmedValue.length() < min || trimmedValue.length() > max) {
            throw badRequest(field + " must contain between " + min + " and " + max + " characters");
        }
        return trimmedValue;
    }

    private ResponseStatusException badRequest(String message) {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
    }
}
