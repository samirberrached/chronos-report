package com.company.chronos.validation;

import com.company.chronos.util.Constants;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Validator backing {@link ValidMonthPeriod}. Ensures the value matches the
 * {@code yyyy-MM} format and does not represent a future month.
 */
public class ValidMonthPeriodValidator
        implements ConstraintValidator<ValidMonthPeriod, String> {

    /** Formatter for the canonical reporting-month format. */
    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern(Constants.MONTH_FORMAT);

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isBlank()) {
            return false;
        }
        YearMonth parsed;
        try {
            parsed = YearMonth.parse(value, FORMATTER);
        } catch (DateTimeParseException ex) {
            return false;
        }
        return !parsed.isAfter(YearMonth.now());
    }
}