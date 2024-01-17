package fr.ouestfrance.querydsl.postgrest.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Concrete implementation of a Pageable
 */
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@Getter
public class PageRequest implements Pageable {

    /**
     * Page number
     */
    private final int pageNumber;
    /**
     * Page size
     */
    private final int pageSize;
    /**
     * Sort
     */
    private final Sort sort;


}
