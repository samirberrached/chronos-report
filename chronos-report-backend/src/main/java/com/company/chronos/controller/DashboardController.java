package com.company.chronos.controller;

import com.company.chronos.dto.DashboardResponse;
import com.company.chronos.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller exposing the aggregated dashboard overview.
 */
@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    /** Dashboard service. */
    private final DashboardService dashboardService;

    /**
     * Returns the dashboard overview for the current reporting month.
     *
     * @return the dashboard response
     */
    @GetMapping
    public ResponseEntity<DashboardResponse> getDashboard() {
        return ResponseEntity.ok(dashboardService.getDashboard());
    }
}