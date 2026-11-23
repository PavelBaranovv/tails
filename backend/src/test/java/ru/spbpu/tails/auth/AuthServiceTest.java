package ru.spbpu.tails.auth;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.spbpu.tails.auth.dto.LoginRequest;
import ru.spbpu.tails.auth.dto.LoginResponse;
import ru.spbpu.tails.auth.exception.InvalidCredentialsException;
import ru.spbpu.tails.common.entity.Shelter;
import ru.spbpu.tails.common.entity.ShelterStatus;
import ru.spbpu.tails.common.repository.ShelterRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock ShelterRepository shelterRepository;
    @Mock PasswordEncoder passwordEncoder;
    @Mock JwtService jwtService;

    @InjectMocks AuthService authService;

    private Shelter activeShelter() {
        Shelter s = new Shelter();
        s.setId(1L);
        s.setEmail("shelter@tails.local");
        s.setPasswordHash("hash");
        s.setStatus(ShelterStatus.ACTIVE);
        return s;
    }

    @Test
    void authServiceShouldReturnTokenWhenShelterIsActive() {
        Shelter shelter = activeShelter();
        when(shelterRepository.findByEmail("shelter@tails.local")).thenReturn(Optional.of(shelter));
        when(passwordEncoder.matches("password123", "hash")).thenReturn(true);
        when(jwtService.generateToken(1L, "shelter@tails.local", "SHELTER")).thenReturn("jwt-token");

        LoginResponse response = authService.login(new LoginRequest("shelter@tails.local", "password123"));

        assertEquals("jwt-token", response.token());
    }

    @Test
    void authServiceShouldRejectLoginWhenPasswordIsWrong() {
        Shelter shelter = activeShelter();
        when(shelterRepository.findByEmail("shelter@tails.local")).thenReturn(Optional.of(shelter));
        when(passwordEncoder.matches("wrong", "hash")).thenReturn(false);

        assertThrows(InvalidCredentialsException.class,
                () -> authService.login(new LoginRequest("shelter@tails.local", "wrong")));
    }

    @Test
    void authServiceShouldRejectLoginWhenShelterNotFound() {
        when(shelterRepository.findByEmail("none@tails.local")).thenReturn(Optional.empty());

        assertThrows(InvalidCredentialsException.class,
                () -> authService.login(new LoginRequest("none@tails.local", "password123")));
    }

    @Test
    void authServiceShouldRejectLoginWhenShelterIsNotActive() {
        Shelter shelter = activeShelter();
        shelter.setStatus(ShelterStatus.PENDING);
        when(shelterRepository.findByEmail("shelter@tails.local")).thenReturn(Optional.of(shelter));
        when(passwordEncoder.matches("password123", "hash")).thenReturn(true);

        assertThrows(InvalidCredentialsException.class,
                () -> authService.login(new LoginRequest("shelter@tails.local", "password123")));
    }
}
