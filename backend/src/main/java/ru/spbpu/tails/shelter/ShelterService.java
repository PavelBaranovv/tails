package ru.spbpu.tails.shelter;

import java.security.Principal;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.spbpu.tails.common.entity.AdoptionApplication;
import ru.spbpu.tails.common.entity.ApplicationStatus;
import ru.spbpu.tails.common.entity.Animal;
import ru.spbpu.tails.common.entity.AnimalStatus;
import ru.spbpu.tails.common.entity.District;
import ru.spbpu.tails.common.entity.Shelter;
import ru.spbpu.tails.common.entity.Species;
import ru.spbpu.tails.common.repository.ApplicationRepository;
import ru.spbpu.tails.common.repository.AnimalRepository;
import ru.spbpu.tails.common.repository.DistrictRepository;
import ru.spbpu.tails.common.repository.ShelterRepository;

@Service
public class ShelterService {

    private final ShelterRepository shelterRepository;
    private final DistrictRepository districtRepository;
    private final AnimalRepository animalRepository;
    private final ApplicationRepository applicationRepository;

    public ShelterService(
            ShelterRepository shelterRepository,
            DistrictRepository districtRepository,
            AnimalRepository animalRepository,
            ApplicationRepository applicationRepository) {
        this.shelterRepository = shelterRepository;
        this.districtRepository = districtRepository;
        this.animalRepository = animalRepository;
        this.applicationRepository = applicationRepository;
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

    @Transactional(readOnly = true)
    public List<ShelterPetResponse> getPets(
            Principal principal,
            AnimalStatus status,
            Species species) {
        Shelter shelter = getShelter(principal);
        AnimalStatus selectedStatus = status == null ? AnimalStatus.AVAILABLE : status;

        List<Animal> animals = species == null
                ? animalRepository.findShelterPetsByStatus(
                        shelter.getId(), selectedStatus)
                : animalRepository.findShelterPetsByStatusAndSpecies(
                        shelter.getId(), selectedStatus, species);

        return animals.stream()
                .map(animal -> new ShelterPetResponse(
                        animal.getId(),
                        animal.getPhotos().stream()
                                .filter(photo -> photo.isMain())
                                .map(photo -> photo.getObjectKey())
                                .findFirst()
                                .orElse(null),
                        animal.getSpecies(),
                        animal.getSpeciesOther(),
                        animal.getName(),
                        getAge(animal),
                        animal.getBirthMonth(),
                        animal.getBirthYear(),
                        animal.getGender(),
                        animal.getBreed(),
                        animal.getStatus()))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ShelterApplicationResponse> getApplications(
            Principal principal,
            ApplicationStatus status) {
        Shelter shelter = getShelter(principal);

        List<AdoptionApplication> applications = status == null
                ? applicationRepository.findShelterApplications(shelter.getId())
                : applicationRepository.findShelterApplicationsByStatus(shelter.getId(), status);

        return applications.stream()
                .map(application -> new ShelterApplicationResponse(
                        application.getId(),
                        application.getStatus(),
                        application.getApplicantName(),
                        application.getApplicantPhone(),
                        application.getAnimal().getId(),
                        application.getAnimal().getName()))
                .toList();
    }

    private String getAge(Animal animal) {
        Short birthYear = animal.getBirthYear();
        if (birthYear == null) {
            return "Возраст неизвестен";
        }

        YearMonth currentMonth = YearMonth.now();
        Short birthMonth = animal.getBirthMonth();
        if (birthMonth == null) {
            int years = currentMonth.getYear() - birthYear;
            return years < 0 ? "Возраст неизвестен" : years + " " + yearWord(years);
        }

        long totalMonths = ChronoUnit.MONTHS.between(
                YearMonth.of(birthYear, birthMonth), currentMonth);
        if (totalMonths < 0) {
            return "Возраст неизвестен";
        }

        int years = (int) (totalMonths / 12);
        int months = (int) (totalMonths % 12);
        if (years == 0) {
            return months + " " + monthWord(months);
        }
        if (months == 0) {
            return years + " " + yearWord(years);
        }
        return years + " " + yearWord(years) + " " + months + " " + monthWord(months);
    }

    private String yearWord(int years) {
        int lastTwoDigits = years % 100;
        if (lastTwoDigits >= 11 && lastTwoDigits <= 14) {
            return "лет";
        }
        return switch (years % 10) {
            case 1 -> "год";
            case 2, 3, 4 -> "года";
            default -> "лет";
        };
    }

    private String monthWord(int months) {
        int lastTwoDigits = months % 100;
        if (lastTwoDigits >= 11 && lastTwoDigits <= 14) {
            return "месяцев";
        }
        return switch (months % 10) {
            case 1 -> "месяц";
            case 2, 3, 4 -> "месяца";
            default -> "месяцев";
        };
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
