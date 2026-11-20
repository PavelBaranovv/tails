package ru.spbpu.tails.application;

import java.security.Principal;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/applications")
public class ApplicationController {

    private final ApplicationService applicationService;

    public ApplicationController(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CreateApplicationResponse createApplication(
            @RequestBody CreateApplicationRequest request) {
        return applicationService.createApplication(request);
    }

    @PatchMapping("/{applicationId}/status")
    public UpdateApplicationStatusResponse changeStatus(
            Principal principal,
            @PathVariable Long applicationId,
            @RequestBody UpdateApplicationStatusRequest request) {
        return applicationService.changeStatus(principal, applicationId, request);
    }
}
