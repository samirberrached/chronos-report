package com.company.chronos.dto;

import com.company.chronos.dto.anomaly.AnomalyResponse;
import com.company.chronos.dto.report.ReportResponse;
import java.math.BigDecimal;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Aggregated payload returned by {@code GET /dashboard}, giving finance
 * analysts a single-call overview of the current reporting period.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardResponse {

    /** The reporting month the dashboard represents (yyyy-MM). */
    private String month;

    /** Total allocated cost across all companies for the month. */
    private BigDecimal totalAllocatedCost;

    /** Total number of employees across all companies. */
    private long totalEmployees;

    /** Count of unresolved anomalies. */
    private long unresolvedAnomalies;

    /** The most recent reports (across companies). */
    private List<ReportResponse> recentReports;

    /** The highest-severity open anomalies. */
    private List<AnomalyResponse> topAnomalies;
}