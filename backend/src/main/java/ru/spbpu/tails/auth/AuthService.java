package ru.spbpu.tails.auth;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.spbpu.tails.auth.dto.LoginRequest;
import ru.spbpu.tails.auth.dto.LoginResponse;
import ru.spbpu.tails.auth.exception.InvalidCredentialsException;
import ru.spbpu.tails.common.entity.Shelter;
import ru.spbpu.tails.common.entity.ShelterStatus;
import ru.spbpu.tails.common.repository.ShelterRepository;

@Service
public class AuthService {

    private final ShelterRepository shelterRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(ShelterRepository shelterRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService) {
        this.shelterRepository = shelterRepository;
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
}
