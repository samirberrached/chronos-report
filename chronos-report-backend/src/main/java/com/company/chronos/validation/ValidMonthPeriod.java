package com.company.chronos.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Validates that a string represents a reporting month in {@code yyyy-MM}
 * format and is not in the future.
 */
@Documented
@Constraint(validatedBy = ValidMonthPeriodValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidMonthPeriod {

    /** Default violation message. */
    String message() default "Month must be a valid yyyy-MM period and not in the future";

    /** Validation groups. */
    Class<?>[] groups() default {};

    /** Custom payload. */
    Class<? extends Payload>[] payload() default {};
}