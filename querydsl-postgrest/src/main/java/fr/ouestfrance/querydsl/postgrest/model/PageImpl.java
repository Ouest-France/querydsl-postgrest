package fr.ouestfrance.querydsl.postgrest.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

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
    private Pageable pageable;
    /**
     * Total elements
     */
    private long totalElements;
    /**
     * Total pages
     */
    private int totalPages;
}
