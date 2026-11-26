package ru.spbpu.tails.admin.service;

import java.util.List;
import java.time.LocalDateTime;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.spbpu.tails.admin.dto.response.ApproveRegistrationResponse;
import ru.spbpu.tails.admin.dto.response.RegistrationRequestListResponse;
import ru.spbpu.tails.admin.dto.response.RegistrationRequestResponse;
import ru.spbpu.tails.common.entity.RegistrationStatus;
import ru.spbpu.tails.common.entity.Shelter;
import ru.spbpu.tails.common.entity.ShelterRegistrationRequest;
import ru.spbpu.tails.common.entity.ShelterStatus;
import ru.spbpu.tails.common.repository.ShelterRegistrationRequestRepository;
import ru.spbpu.tails.common.repository.ShelterRepository;

@Service
public class AdminService {

    private final ShelterRegistrationRequestRepository registrationRequestRepository;
    private final ShelterRepository shelterRepository;

    public AdminService(
            ShelterRegistrationRequestRepository registrationRequestRepository,
            ShelterRepository shelterRepository) {
        this.registrationRequestRepository = registrationRequestRepository;
        this.shelterRepository = shelterRepository;
    }

    @Transactional(readOnly = true)
    public RegistrationRequestListResponse getRegistrationRequests() {
        List<RegistrationRequestResponse> requests = registrationRequestRepository
                .findAllByStatusOrderByCreatedAtAsc(RegistrationStatus.PENDING)
                .stream()
                .map(this::toResponse)
                .toList();

        return new RegistrationRequestListResponse(requests);
    }

    @Transactional
    public ApproveRegistrationResponse approveRegistration(Long registrationRequestId) {
        if (registrationRequestId == null || registrationRequestId < 1) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "registrationRequestId must be a positive number");
        }

        ShelterRegistrationRequest request = registrationRequestRepository
                .findByIdForUpdate(registrationRequestId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Registration request not found"));

        if (request.getStatus() != RegistrationStatus.PENDING) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Registration request has already been processed");
        }
        if (shelterRepository.existsByEmailIgnoreCase(request.getEmail())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "A shelter with this email already exists");
        }

        Shelter shelter = new Shelter();
        shelter.setName(request.getName());
        shelter.setDistrict(request.getDistrict());
        shelter.setAddress(request.getAddress());
        shelter.setPhone(request.getPhone());
        shelter.setEmail(request.getEmail());
        shelter.setPasswordHash(request.getPasswordHash());
        shelter.setStatus(ShelterStatus.ACTIVE);
        shelterRepository.save(shelter);

        request.setStatus(RegistrationStatus.APPROVED);
        request.setProcessedAt(LocalDateTime.now());

        return new ApproveRegistrationResponse(request.getId(), request.getStatus());
    }

    @Transactional
    public void rejectRegistration(Long registrationRequestId) {
        if (registrationRequestId == null || registrationRequestId < 1) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "registrationRequestId must be a positive number");
        }

        ShelterRegistrationRequest request = registrationRequestRepository
                .findByIdForUpdate(registrationRequestId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Registration request not found"));

        if (request.getStatus() != RegistrationStatus.PENDING) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Registration request has already been processed");
        }

        request.setStatus(RegistrationStatus.REJECTED);
        request.setProcessedAt(LocalDateTime.now());
    }

    private RegistrationRequestResponse toResponse(ShelterRegistrationRequest request) {
        return new RegistrationRequestResponse(
                request.getId(),
                request.getName(),
                request.getDistrict().getName(),
                request.getAddress(),
                request.getFullName(),
                request.getPhone(),
                request.getEmail());
    }
}
