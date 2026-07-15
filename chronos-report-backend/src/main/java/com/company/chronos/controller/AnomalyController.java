package com.company.chronos.controller;

import com.company.chronos.dto.anomaly.AnomalyResponse;
import com.company.chronos.dto.anomaly.ResolveAnomalyRequest;
import com.company.chronos.dto.common.PageResponse;
import com.company.chronos.service.AnomalyService;
import com.company.chronos.util.PaginationUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for retrieving and resolving cost-allocation anomalies.
 */
@RestController
@RequestMapping("/api/anomalies")
@RequiredArgsConstructor
public class AnomalyController {

    /** Anomaly service. */
    private final AnomalyService anomalyService;

    /**
     * Lists anomalies for a company (paginated), optionally filtered by status.
     *
     * @param companyId the owning company id
     * @param resolved filter on resolved status (optional)
     * @param page the page number (0-based)
     * @param size the page size
     * @param sortBy the sort property
     * @param direction the sort direction
     * @return a page of anomaly responses
     */
    @GetMapping
    public ResponseEntity<PageResponse<AnomalyResponse>> getAnomalies(
            @RequestParam Long companyId,
            @RequestParam(required = false) Boolean resolved,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {
        Pageable pageable = PaginationUtils.toPageable(page, size, sortBy, direction);
        return ResponseEntity.ok(anomalyService.getAnomalies(companyId, resolved, pageable));
    }

    /**
     * Resolves (or re-opens) an anomaly.
     *
     * @param id the anomaly id
     * @param request the resolve request
     * @return the updated anomaly response
     */
    @PostMapping("/{id}/resolve")
    public ResponseEntity<AnomalyResponse> resolveAnomaly(
            @PathVariable Long id, @Valid @RequestBody ResolveAnomalyRequest request) {
        return ResponseEntity.ok(anomalyService.resolveAnomaly(id, request));
    }
}