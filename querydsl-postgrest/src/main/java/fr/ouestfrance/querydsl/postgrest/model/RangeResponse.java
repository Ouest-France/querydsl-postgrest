package fr.ouestfrance.querydsl.postgrest.model;

import java.util.List;

/**
 * Postgrest range response
 *
 * @param <T>   type of the page
 * @param data  Elements returned
 * @param range Range returned (first returned, last returned, total elements)
 */
public record RangeResponse<T>(List<T> data, Range range) {

    /**
     * Create a range response from a list of elements
     *
     * @param items elements
     * @param <T>   type of the elements
     * @return range response
     */
    @SafeVarargs
    public static <T> RangeResponse<T> of(T... items) {
        return new RangeResponse<>(List.of(items), null);
    }

    /**
     * Retrieve total elements from range response
     *
     * @return total elements
     */
    public long getTotalElements() {
        return (range != null) ? range.getTotalElements() : data.size();
    }

    /**
     * Retrieve page size from range response
     *
     * @return page size
     */
    public int getPageSize() {
        if (range != null) {
            return range.getLimit() - range.getOffset() + 1;
        }
        return data.isEmpty() ? 1 : data.size();
    }
}
