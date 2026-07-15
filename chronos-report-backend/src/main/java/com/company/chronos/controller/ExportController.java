package com.company.chronos.controller;

import com.company.chronos.dto.report.ExportRequest;
import com.company.chronos.service.ExportService;
import com.company.chronos.service.ExportService.ExportResult;
import com.company.chronos.service.ValidationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller exposing report exports (CSV / Excel) for Power BI and
 * downstream consumers.
 */
@RestController
@RequestMapping("/api/reports/export")
@RequiredArgsConstructor
public class ExportController {

    /** Export service. */
    private final ExportService exportService;

    /** Validation service. */
    private final ValidationService validationService;

    /**
     * Exports allocations for a company and month in the requested format.
     *
     * @param companyId the owning company id
     * @param month the reporting month (yyyy-MM)
     * @param format the export format ("csv" or "excel")
     * @return the file bytes with the appropriate content type and filename
     */
    @GetMapping
    public ResponseEntity<byte[]> export(
            @RequestParam Long companyId,
            @RequestParam String month,
            @RequestParam String format) {
        ExportRequest request = ExportRequest.builder()
                .companyId(companyId)
                .month(month)
                .format(format)
                .build();
        validationService.validateExportRequest(request);
        ExportResult result = exportService.exportReport(request);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(result.contentType()));
        headers.setContentDispositionFormData("attachment", result.filename());

        return ResponseEntity.status(HttpStatus.OK)
                .headers(headers)
                .body(result.bytes());
    }
}