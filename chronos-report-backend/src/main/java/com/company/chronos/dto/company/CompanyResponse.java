package com.company.chronos.dto.company;

import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Response payload representing a {@code Company}.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompanyResponse {

    /** Surrogate primary key. */
    private Long id;

    /** Business key: short unique company code. */
    private String companyCode;

    /** Legal name. */
    private String name;

    /** ISO-4217 currency code. */
    private String currency;

    /** Whether the company is active. */
    private boolean active;

    /** Audit: creation instant. */
    private Instant createdAt;

    /** Audit: last modification instant. */
    private Instant lastModifiedAt;
}