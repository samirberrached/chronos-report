package com.company.chronos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Chronos Backend entry point.
 *
 * <p>Bootstraps the Spring Boot application and enables JPA auditing so that
 * {@code @CreatedDate} / {@code @LastModifiedDate} columns are populated
 * automatically on every managed entity.</p>
 */
@SpringBootApplication
@EnableJpaAuditing
public class ChronosBackendApplication {

    /**
     * Application entry point.
     *
     * @param args command-line arguments passed to the Spring Boot runtime
     */
    public static void main(String[] args) {
        SpringApplication.run(ChronosBackendApplication.class, args);
    }
}