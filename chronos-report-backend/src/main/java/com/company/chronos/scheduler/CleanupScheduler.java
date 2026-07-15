package com.company.chronos.scheduler;

import com.company.chronos.entity.EmployeeReport;
import com.company.chronos.repository.EmployeeReportRepository;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Scheduler that purges stale, failed report headers older than a retention
 * window to keep the reporting schema tidy.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class CleanupScheduler {

    /** Report repository. */
    private final EmployeeReportRepository reportRepository;

    /** Retention window in months for failed reports. */
    private static final int RETENTION_MONTHS = 6;

    /**
     * Runs nightly at 03:00, deleting failed reports older than the retention
     * window.
     */
    @Scheduled(cron = "0 0 3 * * *")
    @Transactional
    public void cleanupFailedReports() {
        LocalDate cutoff = LocalDate.now().minusMonths(RETENTION_MONTHS);
        List<EmployeeReport> stale = reportRepository
                .findByStatusAndCreatedAtBefore(
                        com.company.chronos.entity.EmployeeReport.ReportStatus.FAILED,
                        cutoff);
        if (!stale.isEmpty()) {
            reportRepository.deleteAll(stale);
            log.info("Cleaned up {} stale failed reports before {}", stale.size(), cutoff);
        }
    }
}