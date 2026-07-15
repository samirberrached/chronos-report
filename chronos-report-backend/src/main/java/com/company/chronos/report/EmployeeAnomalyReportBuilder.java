package com.company.chronos.report;

import com.company.chronos.entity.Employee;
import com.company.chronos.entity.EmployeeAnomaly;
import com.company.chronos.entity.EmployeeAnomaly.AnomalySeverity;
import com.company.chronos.entity.EmployeeReport;
import com.company.chronos.entity.EmployeeTime;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Pure domain logic that detects data-quality and business-rule anomalies from
 * an employee's time entries and monthly cost, producing
 * {@link EmployeeAnomaly} records attached to a report.
 *
 * <p>Detects: over-allocation (logged hours exceed standard monthly hours),
 * missing time (no entries at all), and zero-cost employees. No persistence or
 * HTTP concerns are present.</p>
 */
public final class EmployeeAnomalyReportBuilder {

    /** Threshold (fraction of standard hours) above which a month is "over". */
    private static final BigDecimal OVER_ALLOCATION_THRESHOLD = BigDecimal.valueOf(1.2);

    /** Private constructor to prevent instantiation of a utility/domain class. */
    private EmployeeAnomalyReportBuilder() {
    }

    /**
     * Builds the list of anomalies for a single employee within a report.
     *
     * @param employee the employee to evaluate
     * @param timeEntries the employee's time entries for the month
     * @param report the report the anomalies belong to
     * @param month the reporting month (yyyy-MM)
     * @return the list of detected anomalies (may be empty)
     */
    public static List<EmployeeAnomaly> buildAnomalies(Employee employee,
                                                       List<EmployeeTime> timeEntries,
                                                       EmployeeReport report,
                                                       String month) {
        List<EmployeeAnomaly> anomalies = new ArrayList<>();

        if (timeEntries == null || timeEntries.isEmpty()) {
            anomalies.add(build(employee, report, month, AnomalySeverity.WARNING,
                    "MISSING_TIME", "No time entries recorded for the month."));
            return anomalies;
        }

        BigDecimal totalHours = timeEntries.stream()
                .map(EmployeeTime::getHours)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal standardHours = BigDecimal.valueOf(
                employee.getStandardMonthlyHours());

        if (standardHours.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal ratio = totalHours.divide(standardHours, 4,
                    java.math.RoundingMode.HALF_UP);
            if (ratio.compareTo(OVER_ALLOCATION_THRESHOLD) > 0) {
                anomalies.add(build(employee, report, month, AnomalySeverity.CRITICAL,
                        "OVER_ALLOCATION",
                        "Logged hours " + totalHours + " exceed standard "
                                + standardHours + " by more than 20%."));
            }
        }

        if (employee.getMonthlyCost() == null
                || employee.getMonthlyCost().compareTo(BigDecimal.ZERO) <= 0) {
            anomalies.add(build(employee, report, month, AnomalySeverity.INFO,
                    "ZERO_COST", "Employee has zero or null monthly cost."));
        }

        return anomalies;
    }

    /**
     * Helper to construct an {@link EmployeeAnomaly}.
     */
    private static EmployeeAnomaly build(Employee employee, EmployeeReport report,
                                         String month, AnomalySeverity severity,
                                         String type, String description) {
        return EmployeeAnomaly.builder()
                .employee(employee)
                .report(report)
                .month(month)
                .severity(severity)
                .type(type)
                .description(description)
                .resolved(false)
                .build();
    }
}