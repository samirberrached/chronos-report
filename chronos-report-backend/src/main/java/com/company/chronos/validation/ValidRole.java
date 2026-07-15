package com.company.chronos.validation;

import com.company.chronos.util.Constants;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Validates that a string is one of the supported application roles
 * ({@code ADMIN}, {@code FINANCE_ANALYST}, {@code VIEWER}).
 */
@Documented
@Constraint(validatedBy = ValidRoleValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidRole {

    /** Default violation message. */
    String message() default "Role must be one of: ADMIN, FINANCE_ANALYST, VIEWER";

    /** Validation groups. */
    Class<?>[] groups() default {};

    /** Custom payload. */
    Class<? extends Payload>[] payload() default {};
}