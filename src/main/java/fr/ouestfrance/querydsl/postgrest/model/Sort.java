package fr.ouestfrance.querydsl.postgrest.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.List;

/**
 * Sort option for queries. You have to provide at least a list of properties to sort
 * The direction defaults to ASC.
 */
@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class Sort {

    /**
     * List of orders queries
     */
    private final List<Order> orders;

    /**
     * Create sort from list of properties
     *
     * @param properties list of ordered keys to sort
     * @return Sort object with ascending direction
     */
    public static Sort by(String... properties) {
        return by(Direction.ASC, properties);
    }

    /**
     * Create sort from list of properties and direction
     *
     * @param direction  direction (ASC, DESC)
     * @param properties list of ordered keys to sort
     * @return Sort object with specified direction
     */
    public static Sort by(Direction direction, String... properties) {
        return new Sort(Arrays.stream(properties)
                .map(x -> new Order(x, direction, NullHandling.NATIVE))
                .toList());
    }

    /**
     * Create sort from list of Order
     *
     * @param orders list of orders
     * @return Sort object with specified orders
     */
    public static Sort by(Sort.Order... orders) {
        return new Sort(Arrays.stream(orders).toList());
    }


    /**
     * Transform a sort to ascending sort
     *
     * @return ascending sort
     */
    public Sort ascending() {
        orders.forEach(x -> x.direction = Direction.ASC);
        return this;
    }

    /**
     * Transform a sort to descending sort
     *
     * @return descending sort
     */
    public Sort descending() {
        orders.forEach(x -> x.direction = Direction.DESC);
        return this;
    }

    /**
     * Sort direction
     */
    public enum Direction {
        /**
         * Ascending : from A to Z
         */
        ASC,
        /**
         * Descending : from Z to A
         */
        DESC
    }

    /**
     * Null Handling sort gesture
     */
    public enum NullHandling {
        /**
         * No null handling
         */
        NATIVE,
        /**
         * get nulls on top positions
         */
        NULLS_FIRST,
        /**
         * get nulls on last position
         */
        NULLS_LAST
    }

    /**
     * Order representation
     */
    @Getter
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Order {
        /**
         * property to filter
         */
        private final String property;
        /**
         * sort direction
         */
        private Direction direction;
        /**
         * Null Handling gesture
         */
        private NullHandling nullHandling;

        /**
         * Create ascending sort on property
         *
         * @param property property to sort
         * @return ascending sort
         */
        public static Order asc(String property) {
            return new Order(property, Direction.ASC, NullHandling.NATIVE);
        }

        /**
         * Create descending sort on property
         *
         * @param property property to sort
         * @return descending sort
         */
        public static Order desc(String property) {
            return new Order(property, Direction.DESC, NullHandling.NATIVE);
        }

        /**
         * Allow to retrieve nulls values first
         *
         * @return order
         */
        public Order nullsFirst() {
            nullHandling = NullHandling.NULLS_FIRST;
            return this;
        }

        /**
         * Allow to retrieve nulls values last
         *
         * @return order
         */
        public Order nullsLast() {
            nullHandling = NullHandling.NULLS_LAST;
            return this;
        }

    }

}
