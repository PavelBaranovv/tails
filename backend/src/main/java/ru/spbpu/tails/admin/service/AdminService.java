package ru.spbpu.tails.admin.service;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.spbpu.tails.admin.dto.response.RegistrationRequestListResponse;
import ru.spbpu.tails.admin.dto.response.RegistrationRequestResponse;
import ru.spbpu.tails.common.entity.RegistrationStatus;
import ru.spbpu.tails.common.entity.ShelterRegistrationRequest;
import ru.spbpu.tails.common.repository.ShelterRegistrationRequestRepository;

@Service
public class AdminService {

    private final ShelterRegistrationRequestRepository registrationRequestRepository;

    public AdminService(ShelterRegistrationRequestRepository registrationRequestRepository) {
        this.registrationRequestRepository = registrationRequestRepository;
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
