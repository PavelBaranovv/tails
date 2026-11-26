package ru.spbpu.tails.auth;

import java.util.Locale;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.spbpu.tails.auth.dto.LoginRequest;
import ru.spbpu.tails.auth.dto.LoginResponse;
import ru.spbpu.tails.auth.dto.RegisterShelterRequest;
import ru.spbpu.tails.auth.dto.RegisterShelterResponse;
import ru.spbpu.tails.auth.exception.InvalidCredentialsException;
import ru.spbpu.tails.common.entity.District;
import ru.spbpu.tails.common.entity.RegistrationStatus;
import ru.spbpu.tails.common.entity.Shelter;
import ru.spbpu.tails.common.entity.ShelterRegistrationRequest;
import ru.spbpu.tails.common.entity.ShelterStatus;
import ru.spbpu.tails.common.repository.DistrictRepository;
import ru.spbpu.tails.common.repository.ShelterRegistrationRequestRepository;
import ru.spbpu.tails.common.repository.ShelterRepository;

@Service
public class AuthService {

    private final ShelterRepository shelterRepository;
    private final ShelterRegistrationRequestRepository registrationRequestRepository;
    private final DistrictRepository districtRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(
            ShelterRepository shelterRepository,
            ShelterRegistrationRequestRepository registrationRequestRepository,
            DistrictRepository districtRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService) {
        this.shelterRepository = shelterRepository;
        this.registrationRequestRepository = registrationRequestRepository;
        this.districtRepository = districtRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public LoginResponse login(LoginRequest request) {
        Shelter shelter = shelterRepository.findByEmail(request.email())
                .orElseThrow(InvalidCredentialsException::new);

        if (!passwordEncoder.matches(request.password(), shelter.getPasswordHash())) {
            throw new InvalidCredentialsException();
        }

        if (shelter.getStatus() != ShelterStatus.ACTIVE) {
            throw new InvalidCredentialsException();
        }

        String token = jwtService.generateToken(
                shelter.getId(),
                shelter.getEmail(),
                "SHELTER"
        );
        return new LoginResponse(token);
    }

    @Transactional
    public RegisterShelterResponse createRegistrationRequest(RegisterShelterRequest request) {
        String name = validateTrimmedLength(request.name(), "name", 2, 256);
        String districtName = validateTrimmedLength(request.district(), "district", 1, 128);
        String address = validateTrimmedLength(request.address(), "address", 2, 256);
        String contactName = validateTrimmedLength(
                request.contactName(), "contactName", 2, 128);
        String phone = validateTrimmedLength(request.phone(), "phone", 7, 18);

        String email = request.email().trim().toLowerCase(Locale.ROOT);
        if (shelterRepository.existsByEmailIgnoreCase(email)
                || registrationRequestRepository.existsByEmailIgnoreCaseAndStatus(
                        email, RegistrationStatus.PENDING)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "A shelter or registration request with this email already exists");
        }

        District district = districtRepository.findByNameIgnoreCase(districtName)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Unknown district: " + districtName));

        ShelterRegistrationRequest registrationRequest = new ShelterRegistrationRequest();
        registrationRequest.setName(name);
        registrationRequest.setDistrict(district);
        registrationRequest.setAddress(address);
        registrationRequest.setFullName(contactName);
        registrationRequest.setPhone(phone);
        registrationRequest.setEmail(email);
        registrationRequest.setPasswordHash(passwordEncoder.encode(request.password()));
        registrationRequest.setStatus(RegistrationStatus.PENDING);

        ShelterRegistrationRequest savedRequest =
                registrationRequestRepository.save(registrationRequest);
        return new RegisterShelterResponse(savedRequest.getId(), savedRequest.getStatus());
    }

    private String validateTrimmedLength(
            String value,
            String field,
            int min,
            int max) {
        String trimmedValue = value.trim();
        if (trimmedValue.length() < min || trimmedValue.length() > max) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    field + " must contain between " + min + " and " + max + " characters");
        }
        return trimmedValue;
    }
}
