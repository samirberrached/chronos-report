package com.company.chronos.service;

import com.company.chronos.dto.employee.EmployeeRequest;
import com.company.chronos.dto.report.GenerateReportRequest;
import com.company.chronos.dto.report.ExportRequest;

/**
 * Service contract for cross-field / business-rule validation that goes beyond
 * Bean Validation, executed in the service layer before persistence or
 * processing.
 */
public interface ValidationService {

    /**
     * Validates an employee creation/update request, throwing
     * {@code ValidationFailedException} on failure.
     *
     * @param request the employee request to validate
     */
    void validateEmployeeRequest(EmployeeRequest request);

    /**
     * Validates a report generation request.
     *
     * @param request the generation request to validate
     */
    void validateGenerateReportRequest(GenerateReportRequest request);

    /**
     * Validates an export request (format, month, company).
     *
     * @param request the export request to validate
     */
    void validateExportRequest(ExportRequest request);
}