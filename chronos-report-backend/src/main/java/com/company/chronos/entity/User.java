package com.company.chronos.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * JPA entity representing an application user (the principal behind a JWT).
 *
 * <p>Credentials are stored as a BCrypt hash. The {@code role} is one of the
 * supported application roles (ADMIN, FINANCE_ANALYST, VIEWER).</p>
 */
@Entity
@Table(
        name = "app_user",
        schema = "reporting",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_app_user_email", columnNames = "email")
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User extends Auditable {

    /** Surrogate primary key. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Unique login email. */
    @Column(name = "email", nullable = false, length = 255)
    private String email;

    /** BCrypt-hashed password. */
    @Column(name = "password", nullable = false)
    private String password;

    /** Display name. */
    @Column(name = "full_name", nullable = false, length = 200)
    private String fullName;

    /** Application role. */
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 20)
    private Role role;

    /** Whether the account is enabled. */
    @Column(name = "enabled", nullable = false)
    private boolean enabled;

    /** Supported application roles. */
    public enum Role {
        /** Full administrative access. */
        ADMIN,
        /** Can generate reports and resolve anomalies. */
        FINANCE_ANALYST,
        /** Read-only access to reports and dashboards. */
        VIEWER
    }
}