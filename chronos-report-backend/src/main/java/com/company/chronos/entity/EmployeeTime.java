package com.company.chronos.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Fact entity recording the raw time an {@link Employee} spent on an
 * {@link Activity} for a {@link Product} on a given {@link Calendar} day.
 *
 * <p>This is the lowest-grain fact in the model; allocations and reports are
 * derived from it.</p>
 */
@Entity
@Table(
        name = "employee_time",
        schema = "reporting",
        indexes = {
                @Index(name = "idx_emptime_employee", columnList = "employee_id"),
                @Index(name = "idx_emptime_calendar", columnList = "calendar_id"),
                @Index(name = "idx_emptime_product", columnList = "product_id"),
                @Index(name = "idx_emptime_activity", columnList = "activity_id")
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeTime extends Auditable {

    /** Surrogate primary key. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Employee who logged the time. */
    @ManyToOne(fetch = jakarta.persistence.FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    /** Calendar day the time was logged against. */
    @ManyToOne(fetch = jakarta.persistence.FetchType.LAZY)
    @JoinColumn(name = "calendar_id", nullable = false)
    private Calendar calendar;

    /** Product the time was spent on. */
    @ManyToOne(fetch = jakarta.persistence.FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    /** Activity performed. */
    @ManyToOne(fetch = jakarta.persistence.FetchType.LAZY)
    @JoinColumn(name = "activity_id", nullable = false)
    private Activity activity;

    /** Number of hours logged. */
    @Column(name = "hours", nullable = false, precision = 8, scale = 2)
    private BigDecimal hours;

    /** Free-text comment describing the time entry. */
    @Column(name = "comment", length = 500)
    private String comment;

    /** Reporting month (yyyy-MM) this entry belongs to, for fast filtering. */
    @Column(name = "month", nullable = false, length = 7)
    private String month;
}