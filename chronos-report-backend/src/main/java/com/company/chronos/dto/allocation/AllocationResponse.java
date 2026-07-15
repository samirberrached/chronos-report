package com.company.chronos.dto.allocation;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Response payload representing an {@code EmployeeAllocation} row, with
 * denormalized names for reporting convenience.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AllocationResponse {

    /** Surrogate primary key. */
    private Long id;

    /** Employee id. */
    private Long employeeId;

    /** Employee display name. */
    private String employeeName;

    /** Product id. */
    private Long productId;

    /** Product name. */
    private String productName;

    /** Activity id. */
    private Long activityId;

    /** Activity name. */
    private String activityName;

    /** Accounting code id. */
    private Long accountingCodeId;

    /** Accounting code. */
    private String accountingCode;

    /** Reporting month (yyyy-MM). */
    private String month;

    /** Allocated cost amount. */
    private BigDecimal allocatedCost;

    /** Allocation percentage (0-100). */
    private BigDecimal allocationPercentage;
}