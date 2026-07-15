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
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Dimension entity representing a type of activity an employee can perform
 * (e.g. "Development", "Support", "Training").
 *
 * <p>The natural/business key is the {@code activityCode} which is unique per
 * company.</p>
 */
@Entity
@Table(
        name = "activity",
        schema = "reporting",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_activity_code_company", columnNames = {"activity_code", "company_id"})
        },
        indexes = {
                @Index(name = "idx_activity_company", columnList = "company_id")
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Activity extends Auditable {

    /** Surrogate primary key. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Business key: short code identifying the activity within a company. */
    @Column(name = "activity_code", nullable = false, length = 30)
    private String activityCode;

    /** Display name of the activity. */
    @Column(name = "name", nullable = false, length = 200)
    private String name;

    /** Whether this activity is chargeable to a product. */
    @Column(name = "chargeable", nullable = false)
    private boolean chargeable;

    /** Owning company. */
    @ManyToOne(fetch = jakarta.persistence.FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;
}