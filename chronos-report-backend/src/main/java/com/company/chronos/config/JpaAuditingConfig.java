package com.company.chronos.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Enables JPA auditing for the application.
 *
 * <p>Although {@code @EnableJpaAuditing} is also declared on the main
 * application class, this dedicated configuration keeps auditing concerns
 * isolated and explicit. With auditing enabled, entities annotated with
 * {@code @CreatedDate} and {@code @LastModifiedDate} are automatically
 * populated by Spring Data JPA.</p>
 */
@Configuration
@EnableJpaAuditing
public class JpaAuditingConfig {
}