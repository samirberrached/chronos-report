package com.company.chronos.dto.anomaly;

import com.company.chronos.entity.EmployeeAnomaly.AnomalySeverity;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Response payload representing an {@code EmployeeAnomaly}, with denormalized
 * employee and report context.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnomalyResponse {

    /** Surrogate primary key. */
    private Long id;

    /** Related employee id (null for company-wide anomalies). */
    private Long employeeId;

    /** Related employee name (null for company-wide anomalies). */
    private String employeeName;

    /** Related report id. */
    private Long reportId;

    /** Reporting month (yyyy-MM). */
    private String month;

    /** Severity level. */
    private AnomalySeverity severity;

    /** Machine-readable anomaly type code. */
    private String type;

    /** Human-readable description. */
    private String description;

    /** Whether the anomaly has been resolved. */
    private boolean resolved;

    /** Audit: creation instant. */
    private Instant createdAt;
}