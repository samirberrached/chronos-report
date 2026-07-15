package com.company.chronos.controller;

import com.company.chronos.dto.common.PageResponse;
import com.company.chronos.dto.report.GenerateReportRequest;
import com.company.chronos.dto.report.ReportResponse;
import com.company.chronos.service.ReportService;
import com.company.chronos.service.ValidationService;
import com.company.chronos.util.PaginationUtils;
import com.company.chronos.validation.ValidMonthPeriod;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for monthly cost-allocation report retrieval and generation.
 */
@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    /** Report service. */
    private final ReportService reportService;

    /** Validation service. */
    private final ValidationService validationService;

    /**
     * Lists reports for a company (paginated).
     *
     * @param companyId the owning company id
     * @param page the page number (0-based)
     * @param size the page size
     * @param sortBy the sort property
     * @param direction the sort direction
     * @return a page of report responses
     */
    @GetMapping
    public ResponseEntity<PageResponse<ReportResponse>> getReports(
            @RequestParam Long companyId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "month") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {
        Pageable pageable = PaginationUtils.toPageable(page, size, sortBy, direction);
        return ResponseEntity.ok(reportService.getReports(companyId, pageable));
    }

    /**
     * Returns a single report for a company and month.
     *
     * @param companyId the owning company id
     * @param month the reporting month (yyyy-MM)
     * @return the report response
     */
    @GetMapping("/{month}")
    public ResponseEntity<ReportResponse> getReport(
            @RequestParam Long companyId, @PathVariable @ValidMonthPeriod String month) {
        return ResponseEntity.ok(reportService.getReport(companyId, month));
    }

    /**
     * (Re)generates the cost-allocation report for a company and month.
     *
     * @param request the generation request
     * @return the generated report response
     */
    @PostMapping("/generate")
    public ResponseEntity<ReportResponse> generateReport(
            @Valid @RequestBody GenerateReportRequest request) {
        validationService.validateGenerateReportRequest(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(reportService.generateReport(request));
    }
}