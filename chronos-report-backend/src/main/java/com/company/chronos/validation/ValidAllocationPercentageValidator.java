package com.company.chronos.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.math.BigDecimal;

/**
 * Validator backing {@link ValidAllocationPercentage}. Accepts null (optional
 * field) and otherwise requires the value to be within [0, 100].
 */
public class ValidAllocationPercentageValidator
        implements ConstraintValidator<ValidAllocationPercentage, BigDecimal> {

    /** Lower bound for an allocation percentage. */
    private static final BigDecimal MIN = BigDecimal.ZERO;

    /** Upper bound for an allocation percentage. */
    private static final BigDecimal MAX = BigDecimal.valueOf(100);

    @Override
    public boolean isValid(BigDecimal value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        return value.compareTo(MIN) >= 0 && value.compareTo(MAX) <= 0;
    }
}