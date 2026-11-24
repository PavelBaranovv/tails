package ru.spbpu.tails.auth;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.spbpu.tails.auth.dto.LoginRequest;
import ru.spbpu.tails.auth.dto.LoginResponse;
import ru.spbpu.tails.auth.dto.RegisterShelterRequest;
import ru.spbpu.tails.auth.dto.RegisterShelterResponse;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/register-shelter")
    public ResponseEntity<RegisterShelterResponse> registerShelter(
            @Valid @RequestBody RegisterShelterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(authService.createRegistrationRequest(request));
    }
}
