package fr.ouestfrance.querydsl.postgrest.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

/**
 * Concrete implementation of a page
 *
 * @param <T> type of the page
 */
@Getter
@AllArgsConstructor
public class PageImpl<T> implements Page<T> {
    /**
     * Elements
     */
    private final List<T> data;
    /**
     * Request information
     */
    private final Pageable pageable;
    /**
     * Total elements
     */
    private long totalElements;
    /**
     * Total pages
     */
    private int totalPages;

    /**
     * Apply range to a specific Page
     * @param range range to apply
     */
    public void withRange(Range range) {
        totalElements = range.getTotalElements();
        if (totalElements > 0) {
            totalPages = (int) (totalElements / (range.getLimit() - range.getOffset())) + 1;
        }
    }
}
