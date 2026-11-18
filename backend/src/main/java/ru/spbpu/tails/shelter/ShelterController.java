package ru.spbpu.tails.shelter;

import java.security.Principal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/shelter")
public class ShelterController {

    private final ShelterService shelterService;

    public ShelterController(ShelterService shelterService) {
        this.shelterService = shelterService;
    }

    @GetMapping("/profile")
    public ShelterProfile getProfile(Principal principal) {
        return shelterService.getProfile(principal);
    }

    @PutMapping("/profile")
    public ShelterProfile updateProfile(
            Principal principal,
            @RequestBody UpdateShelterProfileRequest request) {
        return shelterService.updateProfile(principal, request);
    }

}
