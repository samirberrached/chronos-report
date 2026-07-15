package com.company.chronos.util;

import com.company.chronos.exception.BusinessRuleViolationException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Date/time helper utilities used across services, schedulers and validators.
 *
 * <p>All month handling uses the canonical {@code yyyy-MM} format defined in
 * {@link Constants#MONTH_FORMAT}.</p>
 */
public final class DateUtils {

    /** Private constructor to prevent instantiation of a utility class. */
    private DateUtils() {
    }

    /** Formatter for the canonical reporting-month format. */
    private static final DateTimeFormatter MONTH_FORMATTER =
            DateTimeFormatter.ofPattern(Constants.MONTH_FORMAT);

    /**
     * Formats a {@link YearMonth} to the canonical {@code yyyy-MM} string.
     *
     * @param yearMonth the year-month to format
     * @return the formatted month string
     */
    public static String formatMonth(YearMonth yearMonth) {
        return yearMonth.format(MONTH_FORMATTER);
    }

    /**
     * Parses a {@code yyyy-MM} string into a {@link YearMonth}.
     *
     * @param month the month string to parse
     * @return the parsed year-month
     * @throws BusinessRuleViolationException if the format is invalid
     */
    public static YearMonth parseMonth(String month) {
        try {
            return YearMonth.parse(month, MONTH_FORMATTER);
        } catch (DateTimeParseException ex) {
            throw new BusinessRuleViolationException(
                    "Month must be in yyyy-MM format, but was: " + month);
        }
    }

    /**
     * Returns the previous reporting month relative to the given month.
     *
     * @param month the reference month (yyyy-MM)
     * @return the previous month in yyyy-MM format
     */
    public static String previousMonth(String month) {
        return formatMonth(parseMonth(month).minusMonths(1));
    }

    /**
     * Returns the first calendar day of the given reporting month.
     *
     * @param month the reporting month (yyyy-MM)
     * @return the first day of that month
     */
    public static LocalDate firstDayOfMonth(String month) {
        return parseMonth(month).atDay(1);
    }

    /**
     * Returns the last calendar day of the given reporting month.
     *
     * @param month the reporting month (yyyy-MM)
     * @return the last day of that month
     */
    public static LocalDate lastDayOfMonth(String month) {
        return parseMonth(month).atEndOfMonth();
    }
}