package com.school.controller.dashboard;

import com.school.dto.dashboard.DashboardResponseDTO;
import com.school.service.dashboard.DashboardService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/schools/{schoolId}/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping
    public ResponseEntity<DashboardResponseDTO> getDashboard(
            @PathVariable Long schoolId) {

        return ResponseEntity.ok(dashboardService.getDashboard(schoolId));
    }
}