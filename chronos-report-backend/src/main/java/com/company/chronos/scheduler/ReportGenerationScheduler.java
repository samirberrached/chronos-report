package com.company.chronos.scheduler;

import com.company.chronos.dto.report.GenerateReportRequest;
import com.company.chronos.entity.Company;
import com.company.chronos.repository.CompanyRepository;
import com.company.chronos.service.ReportService;
import com.company.chronos.util.DateUtils;
import java.time.YearMonth;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Scheduler that triggers monthly cost-allocation report generation for every
 * company at the close of each month.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ReportGenerationScheduler {

    /** Company repository. */
    private final CompanyRepository companyRepository;

    /** Report service. */
    private final ReportService reportService;

    /**
     * Runs on the first day of each month at 02:00, generating the report for
     * the previous month for every company.
     */
    @Scheduled(cron = "0 0 2 1 * *")
    @Transactional
    public void generateMonthlyReports() {
        String previousMonth = DateUtils.formatMonth(YearMonth.now().minusMonths(1));
        log.info("Starting monthly report generation for {}", previousMonth);
        List<Company> companies = companyRepository.findAll();
        for (Company company : companies) {
            try {
                GenerateReportRequest request = GenerateReportRequest.builder()
                        .companyId(company.getId())
                        .month(previousMonth)
                        .build();
                reportService.generateReport(request);
                log.info("Generated report for company {} / {}", company.getId(),
                        previousMonth);
            } catch (Exception ex) {
                log.error("Failed to generate report for company {} / {}",
                        company.getId(), previousMonth, ex);
            }
        }
    }
}