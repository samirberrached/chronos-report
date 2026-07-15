package com.company.chronos.controller;

import com.company.chronos.dto.allocation.AllocationResponse;
import com.company.chronos.service.AllocationService;
import com.company.chronos.validation.ValidMonthPeriod;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller exposing cost-allocation fact rows for a company and month.
 */
@RestController
@RequestMapping("/api/allocations")
@RequiredArgsConstructor
public class AllocationController {

    /** Allocation service. */
    private final AllocationService allocationService;

    /**
     * Returns all allocations for a company and reporting month.
     *
     * @param companyId the owning company id
     * @param month the reporting month (yyyy-MM)
     * @return the list of allocation responses
     */
    @GetMapping
    public ResponseEntity<List<AllocationResponse>> getAllocations(
            @RequestParam Long companyId,
            @RequestParam @ValidMonthPeriod String month) {
        return ResponseEntity.ok(allocationService.getAllocations(companyId, month));
    }
}