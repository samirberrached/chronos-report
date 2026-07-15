package com.company.chronos.service;

import com.company.chronos.dto.common.PageResponse;
import com.company.chronos.dto.report.GenerateReportRequest;
import com.company.chronos.dto.report.ReportResponse;
import org.springframework.data.domain.Pageable;

/**
 * Service contract for monthly cost-allocation report generation and retrieval.
 */
public interface ReportService {

    /**
     * Returns a paginated list of reports for a company.
     *
     * @param companyId the owning company id
     * @param pageable pagination metadata
     * @return a page of report responses
     */
    PageResponse<ReportResponse> getReports(Long companyId, Pageable pageable);

    /**
     * Returns a single report for a company and month.
     *
     * @param companyId the owning company id
     * @param month the reporting month (yyyy-MM)
     * @return the report response
     */
    ReportResponse getReport(Long companyId, String month);

    /**
     * (Re)generates the cost-allocation report for a company and month.
     *
     * @param request the generation request
     * @return the generated report response
     */
    ReportResponse generateReport(GenerateReportRequest request);
}