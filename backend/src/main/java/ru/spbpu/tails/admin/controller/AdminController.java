package ru.spbpu.tails.admin.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.spbpu.tails.admin.dto.response.ApproveRegistrationResponse;
import ru.spbpu.tails.admin.dto.response.RegistrationRequestListResponse;
import ru.spbpu.tails.admin.dto.response.ShelterListResponse;
import ru.spbpu.tails.admin.service.AdminService;

@RestController
@RequestMapping("/api/v1/admin")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/registration-requests")
    public RegistrationRequestListResponse getRegistrationRequests() {
        return adminService.getRegistrationRequests();
    }

    @GetMapping("/shelters")
    public ShelterListResponse getShelters() {
        return adminService.getShelters();
    }

    @PostMapping("/registration-requests/{registrationRequestId}/approve")
    public ApproveRegistrationResponse approveRegistration(
            @PathVariable Long registrationRequestId) {
        return adminService.approveRegistration(registrationRequestId);
    }

    @PostMapping("/registration-requests/{registrationRequestId}/reject")
    public ResponseEntity<Void> rejectRegistration(
            @PathVariable Long registrationRequestId) {
        adminService.rejectRegistration(registrationRequestId);
        return ResponseEntity.noContent().build();
    }
}
