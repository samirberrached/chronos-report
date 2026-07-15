package com.company.chronos.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Dimension entity representing a calendar day used to normalise time entries
 * against working days, weekends and public holidays.
 *
 * <p>The natural/business key is the {@code date} column, which is globally
 * unique.</p>
 */
@Entity
@Table(
        name = "calendar",
        schema = "reporting",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_calendar_date", columnNames = "date")
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Calendar extends Auditable {

    /** Surrogate primary key. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** The calendar date (business key). */
    @Column(name = "date", nullable = false)
    private LocalDate date;

    /** Calendar year of the date. */
    @Column(name = "year", nullable = false)
    private Integer year;

    /** Calendar month (1-12) of the date. */
    @Column(name = "month", nullable = false)
    private Integer month;

    /** Whether the date is a working day (false for weekends/holidays). */
    @Column(name = "working_day", nullable = false)
    private boolean workingDay;
}