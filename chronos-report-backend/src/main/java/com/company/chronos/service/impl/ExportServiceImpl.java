package com.company.chronos.service.impl;

import com.company.chronos.dto.allocation.AllocationResponse;
import com.company.chronos.dto.report.ExportRequest;
import com.company.chronos.export.ExportStrategy;
import com.company.chronos.service.AllocationService;
import com.company.chronos.service.ExportService;
import com.company.chronos.service.ValidationService;
import com.company.chronos.util.Constants;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Default implementation of {@link ExportService}. Selects the appropriate
 * {@link ExportStrategy} based on the requested format and returns the bytes
 * with the correct content type and filename.
 */
@Service
@RequiredArgsConstructor
public class ExportServiceImpl implements ExportService {

    /** Allocation service (source of rows to export). */
    private final AllocationService allocationService;

    /** Validation service. */
    private final ValidationService validationService;

    /** Registered export strategies (csv, excel). */
    private final List<ExportStrategy> strategies;

    @Override
    public ExportResult exportReport(ExportRequest request) {
        validationService.validateExportRequest(request);

        List<AllocationResponse> rows =
                allocationService.getAllocations(request.getCompanyId(), request.getMonth());

        ExportStrategy strategy = strategies.stream()
                .filter(s -> s.getFormat().equalsIgnoreCase(request.getFormat()))
                .findFirst()
                .orElseThrow(() -> new com.company.chronos.exception
                        .BusinessRuleViolationException(
                                "Unsupported export format: " + request.getFormat()));

        byte[] bytes = strategy.export(rows, request.getMonth());
        String contentType = request.getFormat().equalsIgnoreCase("excel")
                ? Constants.CONTENT_TYPE_EXCEL : Constants.CONTENT_TYPE_CSV;
        String filename = "allocations-" + request.getMonth() + "."
                + request.getFormat().toLowerCase();

        return new ExportResult(bytes, contentType, filename);
    }
}