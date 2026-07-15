package com.company.chronos.service.impl;

import com.company.chronos.dto.DashboardResponse;
import com.company.chronos.entity.EmployeeReport;
import com.company.chronos.entity.EmployeeReport.ReportStatus;
import com.company.chronos.repository.EmployeeAnomalyRepository;
import com.company.chronos.repository.EmployeeReportRepository;
import com.company.chronos.service.DashboardService;
import com.company.chronos.util.DateUtils;
import java.math.BigDecimal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Default implementation of {@link DashboardService}. Aggregates the latest
 * reporting month's KPIs for the dashboard.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardServiceImpl implements DashboardService {

    /** Report repository. */
    private final EmployeeReportRepository reportRepository;

    /** Anomaly repository. */
    private final EmployeeAnomalyRepository anomalyRepository;

    @Override
    public DashboardResponse getDashboard() {
        String month = DateUtils.formatMonth(java.time.YearMonth.now());
        List<EmployeeReport> reports = reportRepository.findByMonth(month);

        long totalReports = reports.size();
        long generated = reports.stream()
                .filter(r -> r.getStatus() == ReportStatus.GENERATED).count();
        long failed = reports.stream()
                .filter(r -> r.getStatus() == ReportStatus.FAILED).count();
        BigDecimal totalCost = reports.stream()
                .map(EmployeeReport::getTotalAllocatedCost)
                .filter(java.util.Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        long openAnomalies = anomalyRepository.countByResolvedFalse();

        return DashboardResponse.builder()
                .currentMonth(month)
                .totalReports(totalReports)
                .generatedReports(generated)
                .failedReports(failed)
                .totalAllocatedCost(totalCost)
                .openAnomalies(openAnomalies)
                .build();
    }
}