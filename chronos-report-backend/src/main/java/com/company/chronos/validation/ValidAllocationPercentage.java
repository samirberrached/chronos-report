package com.company.chronos.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Validates that a {@link java.math.BigDecimal} allocation percentage is
 * between 0 and 100 (inclusive).
 */
@Documented
@Constraint(validatedBy = ValidAllocationPercentageValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidAllocationPercentage {

    /** Default violation message. */
    String message() default "Allocation percentage must be between 0 and 100";

    /** Validation groups. */
    Class<?>[] groups() default {};

    /** Custom payload. */
    Class<? extends Payload>[] payload() default {};
}