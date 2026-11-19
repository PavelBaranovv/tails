package ru.spbpu.tails.shelter;

import java.security.Principal;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.spbpu.tails.common.entity.ApplicationStatus;
import ru.spbpu.tails.common.entity.AnimalStatus;
import ru.spbpu.tails.common.entity.Species;

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

    @GetMapping("/pets")
    public List<ShelterPetResponse> getPets(
            Principal principal,
            @RequestParam(required = false) AnimalStatus status,
            @RequestParam(required = false) Species species) {
        return shelterService.getPets(principal, status, species);
    }

    @GetMapping("/applications")
    public List<ShelterApplicationResponse> getApplications(
            Principal principal,
            @RequestParam(required = false) ApplicationStatus status) {
        return shelterService.getApplications(principal, status);
    }

}
