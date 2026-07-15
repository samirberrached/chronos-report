package com.company.chronos.dto.common;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Generic envelope for paginated API responses, mirroring Spring Data's
 * {@code Page} metadata so frontends and Power BI can page consistently.
 *
 * @param <T> the element type contained in the page
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageResponse<T> {

    /** The content for the current page. */
    private List<T> content;

    /** Zero-based page index. */
    private int page;

    /** Page size used. */
    private int size;

    /** Total number of elements across all pages. */
    private long totalElements;

    /** Total number of pages. */
    private int totalPages;

    /** Whether this is the last page. */
    private boolean last;
}