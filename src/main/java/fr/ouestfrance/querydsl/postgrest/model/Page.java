package fr.ouestfrance.querydsl.postgrest.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Representation of a Page, a page is an iterable item which have list of elements, pageable information
 * and totalSizeElements and totalPage
 *
 * @param <T> type of item
 */
public interface Page<T> extends Iterable<T> {

    /**
     * Create simple page from items
     *
     * @param items items
     * @param <T>   type of items
     * @return one page of items
     */
    @SafeVarargs
    static <T> Page<T> of(T... items) {
        return new PageImpl<>(Arrays.asList(items), Pageable.unPaged(), 0, items.length);
    }

    /**
     * Get data for a page
     * @return data for a page
     */
    List<T> getData();

    /**
     * Get page request infomations
     * @return Pageable information with number of elements, number of the page and sort options
     */
    Pageable getPageable();

    /**
     * Get size of page
     * @return size of the data for the current page
     */
    default int size() {
        return getData().size();
    }

    /**
     * Get total elements from the datasource
     * @return total elements
     */
    long getTotalElements();

    /**
     * Get the total pages
     * @return total pages
     */
    int getTotalPages();

    /**
     * Streaming from the page
     * @return stream
     */
    default Stream<T> stream() {
        return getData().stream();
    }

    @Override
    default Iterator<T> iterator() {
        return getData().iterator();
    }

    /**
     * Convert a page
     * @param converter function that convert type to another
     * @return page converted
     * @param <U> type of returned object
     */
    default <U> Page<U> map(Function<T, U> converter) {
        return new PageImpl<>(stream().map(converter).toList(), getPageable(), getTotalElements(), getTotalPages());
    }
}
