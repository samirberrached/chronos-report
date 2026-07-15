package com.company.chronos.report;

import com.company.chronos.entity.Employee;
import com.company.chronos.entity.EmployeeAllocation;
import com.company.chronos.entity.EmployeeTime;
import com.company.chronos.entity.Product;
import com.company.chronos.entity.Activity;
import com.company.chronos.entity.AccountingCode;
import com.company.chronos.exception.BusinessRuleViolationException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Pure domain logic that builds {@link EmployeeAllocation} rows from an
 * employee's logged {@link EmployeeTime} entries for a reporting month.
 *
 * <p>This class contains no HTTP, persistence or framework concerns. It
 * computes, for each (product, activity) combination, the share of the
 * employee's monthly cost proportional to the hours logged, and books the
 * amount against the supplied {@link AccountingCode}.</p>
 */
public final class AnalyticEmployeeTimeReportBuilder {

    /** Private constructor to prevent instantiation of a utility/domain class. */
    private AnalyticEmployeeTimeReportBuilder() {
    }

    /**
     * Builds allocation rows for a single employee from their time entries.
     *
     * @param employee the employee being allocated
     * @param timeEntries the employee's time entries for the month
     * @param accountingCode the accounting code to book allocations against
     * @param month the reporting month (yyyy-MM)
     * @return the list of computed allocations
     * @throws BusinessRuleViolationException if no time was logged
     */
    public static List<EmployeeAllocation> buildAllocations(
            Employee employee,
            List<EmployeeTime> timeEntries,
            AccountingCode accountingCode,
            String month) {

        if (timeEntries == null || timeEntries.isEmpty()) {
            throw new BusinessRuleViolationException(
                    "Cannot build allocations for employee " + employee.getEmployeeCode()
                            + ": no time entries found for month " + month);
        }

        BigDecimal totalHours = timeEntries.stream()
                .map(EmployeeTime::getHours)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (totalHours.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessRuleViolationException(
                    "Total logged hours must be positive for employee "
                            + employee.getEmployeeCode());
        }

        // Aggregate hours by (product, activity) pair.
        Map<Key, BigDecimal> grouped = timeEntries.stream()
                .collect(Collectors.groupingBy(
                        e -> new Key(e.getProduct(), e.getActivity()),
                        Collectors.reducing(BigDecimal.ZERO,
                                EmployeeTime::getHours, BigDecimal::add)));

        List<EmployeeAllocation> allocations = new ArrayList<>();
        for (Map.Entry<Key, BigDecimal> entry : grouped.entrySet()) {
            BigDecimal share = entry.getValue()
                    .divide(totalHours, 4, java.math.RoundingMode.HALF_UP);
            BigDecimal allocatedCost = employee.getMonthlyCost()
                    .multiply(share)
                    .setScale(2, java.math.RoundingMode.HALF_UP);
            BigDecimal percentage = share.multiply(BigDecimal.valueOf(100))
                    .setScale(2, java.math.RoundingMode.HALF_UP);

            allocations.add(EmployeeAllocation.builder()
                    .employee(employee)
                    .product(entry.getKey().product())
                    .activity(entry.getKey().activity())
                    .accountingCode(accountingCode)
                    .month(month)
                    .allocatedCost(allocatedCost)
                    .allocationPercentage(percentage)
                    .build());
        }
        return allocations;
    }

    /** Composite key of (product, activity) used for grouping. */
    private record Key(Product product, Activity activity) {
    }
}