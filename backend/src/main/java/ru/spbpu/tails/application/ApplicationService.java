package ru.spbpu.tails.application;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.spbpu.tails.common.entity.AdoptionApplication;
import ru.spbpu.tails.common.entity.Animal;
import ru.spbpu.tails.common.entity.AnimalStatus;
import ru.spbpu.tails.common.entity.ApplicationStatus;
import ru.spbpu.tails.common.repository.AnimalRepository;
import ru.spbpu.tails.common.repository.ApplicationRepository;

@Service
public class ApplicationService {

    private final AnimalRepository animalRepository;
    private final ApplicationRepository applicationRepository;

    public ApplicationService(
            AnimalRepository animalRepository,
            ApplicationRepository applicationRepository) {
        this.animalRepository = animalRepository;
        this.applicationRepository = applicationRepository;
    }

    @Transactional
    public CreateApplicationResponse createApplication(CreateApplicationRequest request) {
        if (request == null) {
            throw badRequest("Application data is required");
        }
        if (request.animalId() == null || request.animalId() < 1) {
            throw badRequest("animalId must be a positive number");
        }

        String applicantName = validateText(request.applicantName(), "applicantName", 2, 128);
        String applicantPhone = validateText(request.applicantPhone(), "applicantPhone", 7, 18);

        Animal animal = animalRepository.findById(request.animalId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Animal not found"));
        if (animal.getStatus() != AnimalStatus.AVAILABLE) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Applications can only be submitted for available animals");
        }

        AdoptionApplication application = new AdoptionApplication();
        application.setAnimal(animal);
        application.setShelter(animal.getShelter());
        application.setApplicantName(applicantName);
        application.setApplicantPhone(applicantPhone);
        application.setStatus(ApplicationStatus.NEW);

        AdoptionApplication savedApplication = applicationRepository.save(application);
        return new CreateApplicationResponse(
                savedApplication.getId(),
                savedApplication.getStatus());
    }

    private String validateText(String value, String field, int min, int max) {
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
