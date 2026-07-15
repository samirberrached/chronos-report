package com.company.chronos.validation;

import com.company.chronos.util.Constants;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Validator backing {@link ValidRole}. Checks the value against the supported
 * role constants.
 */
public class ValidRoleValidator implements ConstraintValidator<ValidRole, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isBlank()) {
            return false;
        }
        return switch (value) {
            case Constants.ROLE_ADMIN, Constants.ROLE_FINANCE_ANALYST,
                 Constants.ROLE_VIEWER -> true;
            default -> false;
        };
    }
}