package com.company.chronos.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Fact entity recording a data-quality or business-rule anomaly detected
 * during report generation (e.g. over-allocation, missing time, duplicate
 * entries).
 *
 * <p>Anomalies are surfaced to finance analysts via the anomalies endpoint and
 * the anomaly report builder.</p>
 */
@Entity
@Table(
        name = "employee_anomaly",
        schema = "reporting",
        indexes = {
                @Index(name = "idx_anomaly_employee", columnList = "employee_id"),
                @Index(name = "idx_anomaly_report", columnList = "report_id"),
                @Index(name = "idx_anomaly_month", columnList = "month")
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeAnomaly extends Auditable {

    /** Surrogate primary key. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Employee the anomaly relates to (nullable for company-wide anomalies). */
    @ManyToOne(fetch = jakarta.persistence.FetchType.LAZY)
    @JoinColumn(name = "employee_id")
    private Employee employee;

    /** Report the anomaly was detected during. */
    @ManyToOne(fetch = jakarta.persistence.FetchType.LAZY)
    @JoinColumn(name = "report_id", nullable = false)
    private EmployeeReport report;

    /** Reporting month (yyyy-MM) the anomaly belongs to. */
    @Column(name = "month", nullable = false, length = 7)
    private String month;

    /** Severity of the anomaly. */
    @Enumerated(EnumType.STRING)
    @Column(name = "severity", nullable = false, length = 20)
    private AnomalySeverity severity;

    /** Machine-readable anomaly type code. */
    @Column(name = "type", nullable = false, length = 50)
    private String type;

    /** Human-readable description of the anomaly. */
    @Column(name = "description", nullable = false, length = 500)
    private String description;

    /** Whether the anomaly has been acknowledged by an analyst. */
    @Column(name = "resolved", nullable = false)
    private boolean resolved;

    /** Enumeration of anomaly severity levels. */
    public enum AnomalySeverity {
        /** Informational, no action required. */
        INFO,
        /** Warning worth reviewing. */
        WARNING,
        /** Critical, blocks correct allocation. */
        CRITICAL
    }
}