package com.company.chronos.util;

import com.company.chronos.exception.BusinessRuleViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

/**
 * Helper for building {@link Pageable} instances with safe defaults and bounds,
 * ensuring clients cannot request unbounded or oversized pages.
 */
public final class PaginationUtils {

    /** Private constructor to prevent instantiation of a utility class. */
    private PaginationUtils() {
    }

    /**
     * Builds a zero-based, bounded {@link Pageable} from raw request parameters.
     *
     * @param page the requested page (0-based); negative values default to 0
     * @param size the requested size; clamped to {@link Constants#MAX_PAGE_SIZE}
     * @param sortBy the property to sort by (defaults to "id" if blank)
     * @param direction the sort direction ("asc"/"desc", defaults to "asc")
     * @return a safe {@link Pageable}
     */
    public static Pageable toPageable(int page, int size, String sortBy, String direction) {
        int safePage = Math.max(page, 0);
        int safeSize = (size <= 0) ? Constants.DEFAULT_PAGE_SIZE
                : Math.min(size, Constants.MAX_PAGE_SIZE);

        String safeSort = (sortBy == null || sortBy.isBlank()) ? "id" : sortBy;
        Sort.Direction sortDirection = "desc".equalsIgnoreCase(direction)
                ? Sort.Direction.DESC : Sort.Direction.ASC;

        return PageRequest.of(safePage, safeSize, Sort.by(sortDirection, safeSort));
    }

    /**
     * Validates that a page size does not exceed the maximum allowed.
     *
     * @param size the requested size
     * @throws BusinessRuleViolationException if the size exceeds the maximum
     */
    public static void assertValidSize(int size) {
        if (size > Constants.MAX_PAGE_SIZE) {
            throw new BusinessRuleViolationException(
                    "Requested page size " + size + " exceeds maximum of "
                            + Constants.MAX_PAGE_SIZE);
        }
    }
}