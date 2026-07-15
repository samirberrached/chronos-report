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
 * Fact entity representing a generated monthly cost-allocation report for a
 * {@link Company}.
 *
 * <p>A report aggregates all {@link EmployeeAllocation} rows for a given
 * reporting month and tracks its generation status.</p>
 */
@Entity
@Table(
        name = "employee_report",
        schema = "reporting",
        indexes = {
                @Index(name = "idx_empreport_company", columnList = "company_id"),
                @Index(name = "idx_empreport_month", columnList = "month")
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeReport extends Auditable {

    /** Surrogate primary key. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Company this report belongs to. */
    @ManyToOne(fetch = jakarta.persistence.FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    /** Reporting month (yyyy-MM) covered by the report. */
    @Column(name = "month", nullable = false, length = 7)
    private String month;

    /** Generation status of the report. */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private ReportStatus status;

    /** Total allocated cost across all employees for the month. */
    @Column(name = "total_allocated_cost", precision = 16, scale = 2)
    private java.math.BigDecimal totalAllocatedCost;

    /** Number of employees included in the report. */
    @Column(name = "employee_count")
    private Integer employeeCount;

    /** Free-text note, e.g. failure reason when status is FAILED. */
    @Column(name = "note", length = 500)
    private String note;

    /** Enumeration of possible report generation states. */
    public enum ReportStatus {
        /** Report is being generated. */
        PENDING,
        /** Report was generated successfully. */
        GENERATED,
        /** Report generation failed. */
        FAILED
    }
}