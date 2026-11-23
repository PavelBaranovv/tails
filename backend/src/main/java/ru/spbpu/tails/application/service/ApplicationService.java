package ru.spbpu.tails.application.service;

import java.security.Principal;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.spbpu.tails.application.dto.request.CreateApplicationRequest;
import ru.spbpu.tails.application.dto.request.UpdateApplicationStatusRequest;
import ru.spbpu.tails.application.dto.response.ApplicationResponse;
import ru.spbpu.tails.application.dto.response.CreateApplicationResponse;
import ru.spbpu.tails.application.dto.response.UpdateApplicationStatusResponse;
import ru.spbpu.tails.common.entity.AdoptionApplication;
import ru.spbpu.tails.common.entity.Animal;
import ru.spbpu.tails.common.entity.AnimalStatus;
import ru.spbpu.tails.common.entity.ApplicationStatus;
import ru.spbpu.tails.common.repository.AnimalRepository;
import ru.spbpu.tails.common.repository.ApplicationRepository;
import ru.spbpu.tails.common.repository.ShelterRepository;

@Service
public class ApplicationService {

    private final AnimalRepository animalRepository;
    private final ApplicationRepository applicationRepository;
    private final ShelterRepository shelterRepository;

    public ApplicationService(
            AnimalRepository animalRepository,
            ApplicationRepository applicationRepository,
            ShelterRepository shelterRepository) {
        this.animalRepository = animalRepository;
        this.applicationRepository = applicationRepository;
        this.shelterRepository = shelterRepository;
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

    @Transactional
    public UpdateApplicationStatusResponse changeStatus(
            Principal principal,
            Long applicationId,
            UpdateApplicationStatusRequest request) {
        if (principal == null || principal.getName() == null || principal.getName().isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Authentication is required");
        }
        if (applicationId == null || applicationId < 1) {
            throw badRequest("applicationId must be a positive number");
        }
        if (request == null || request.status() == null) {
            throw badRequest("status is required");
        }

        var shelter = shelterRepository.findByEmailIgnoreCase(principal.getName())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.FORBIDDEN,
                        "Authenticated account is not a shelter"));
        AdoptionApplication application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Application not found"));

        if (!application.getShelter().getId().equals(shelter.getId())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Application belongs to another shelter");
        }

        application.setStatus(request.status());
        return new UpdateApplicationStatusResponse(
                application.getId(),
                application.getAnimal().getId(),
                application.getApplicantName(),
                application.getApplicantPhone(),
                application.getStatus());
    }

    @Transactional(readOnly = true)
    public List<ApplicationResponse> findByShelter(
            Long shelterId,
            ApplicationStatus status) {
        List<AdoptionApplication> applications = status == null
                ? applicationRepository.findShelterApplications(shelterId)
                : applicationRepository.findShelterApplicationsByStatus(shelterId, status);

        return applications.stream()
                .map(application -> new ApplicationResponse(
                        application.getId(),
                        application.getStatus(),
                        application.getApplicantName(),
                        application.getApplicantPhone(),
                        application.getAnimal().getId(),
                        application.getAnimal().getName()))
                .toList();
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
