package com.company.chronos.dto.report;

import com.company.chronos.entity.EmployeeReport.ReportStatus;
import java.math.BigDecimal;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Response payload representing a generated monthly {@code EmployeeReport}.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportResponse {

    /** Surrogate primary key. */
    private Long id;

    /** Owning company id. */
    private Long companyId;

    /** Owning company name. */
    private String companyName;

    /** Reporting month (yyyy-MM). */
    private String month;

    /** Generation status. */
    private ReportStatus status;

    /** Total allocated cost across all employees for the month. */
    private BigDecimal totalAllocatedCost;

    /** Number of employees included in the report. */
    private Integer employeeCount;

    /** Free-text note (e.g. failure reason). */
    private String note;

    /** Audit: creation instant. */
    private Instant createdAt;

    /** Audit: last modification instant. */
    private Instant lastModifiedAt;
}