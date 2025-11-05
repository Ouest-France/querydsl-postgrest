package fr.ouestfrance.querydsl.postgrest.model;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.function.Function;

/**
 * Utility class for operations on rest paginated API
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PageRequests {

    /**
     * Default page size for API calls
     */
    public static final int DEFAULT_PAGE_SIZE = 1000;

    /**
     * Creates a PageCollector
     *
     * @param collectFunction the collect function that call a paginated API
     * @param options         options on how to call the API
     * @param <T>             the type of the resource
     * @return a PageCollector that can be used to get data from the API
     */
    public static <T> PageCollector<T> collect(Function<PageRequest, Page<T>> collectFunction, PageCollector.PageCollectorOptions options) {
        return new PageCollector<>(collectFunction, options);
    }
}
