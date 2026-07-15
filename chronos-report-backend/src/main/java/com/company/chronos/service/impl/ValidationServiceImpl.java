package com.company.chronos.service.impl;

import com.company.chronos.dto.employee.EmployeeRequest;
import com.company.chronos.dto.report.ExportRequest;
import com.company.chronos.dto.report.GenerateReportRequest;
import com.company.chronos.exception.ValidationFailedException;
import com.company.chronos.service.ValidationService;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Default implementation of {@link ValidationService}. Performs cross-field
 * and business-rule validation that Bean Validation cannot express.
 */
@Service
@RequiredArgsConstructor
public class ValidationServiceImpl implements ValidationService {

    @Override
    public void validateEmployeeRequest(EmployeeRequest request) {
        List<String> errors = new ArrayList<>();
        if (request.getMonthlyCost() != null
                && request.getMonthlyCost().signum() < 0) {
            errors.add("monthlyCost must not be negative");
        }
        if (request.getStandardMonthlyHours() != null
                && request.getStandardMonthlyHours() < 0) {
            errors.add("standardMonthlyHours must not be negative");
        }
        if (!errors.isEmpty()) {
            throw new ValidationFailedException("Employee validation failed", errors);
        }
    }

    @Override
    public void validateGenerateReportRequest(GenerateReportRequest request) {
        List<String> errors = new ArrayList<>();
        if (request.getCompanyId() == null) {
            errors.add("companyId is required");
        }
        if (request.getMonth() == null || request.getMonth().isBlank()) {
            errors.add("month is required");
        }
        if (!errors.isEmpty()) {
            throw new ValidationFailedException("Report request validation failed", errors);
        }
    }

    @Override
    public void validateExportRequest(ExportRequest request) {
        List<String> errors = new ArrayList<>();
        if (request.getCompanyId() == null) {
            errors.add("companyId is required");
        }
        if (request.getMonth() == null || request.getMonth().isBlank()) {
            errors.add("month is required");
        }
        if (request.getFormat() == null
                || (!request.getFormat().equalsIgnoreCase("csv")
                    && !request.getFormat().equalsIgnoreCase("excel"))) {
            errors.add("format must be 'csv' or 'excel'");
        }
        if (!errors.isEmpty()) {
            throw new ValidationFailedException("Export request validation failed", errors);
        }
    }
}