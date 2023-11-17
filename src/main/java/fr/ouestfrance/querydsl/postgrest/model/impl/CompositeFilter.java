package fr.ouestfrance.querydsl.postgrest.model.impl;


import fr.ouestfrance.querydsl.postgrest.builders.FilterVisitor;
import fr.ouestfrance.querydsl.postgrest.builders.QueryFilterVisitor;
import fr.ouestfrance.querydsl.postgrest.model.Filter;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Filter that handle multiple filter with a concrete operator
 * It could be mixed with others composites filters
 * Example :
 * <pre>
 * new CompositeFilter("or",
 *      SimpleFilter.of("date", "gte", "2023-10-10),
 *      SimpleFilter.of("date", "is", null)
 * );
 * </pre>
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class CompositeFilter implements Filter, FilterVisitor {

    /**
     * Operator function (allowed and/or)
     */
    private final String operator;
    /**
     * Filters list
     */
    @Getter
    private final List<Filter> filters = new ArrayList<>();

    /**
     * Static method to create composite filter
     *
     * @param operator type of composite operator (or, and, not)
     * @param left     first filter (required)
     * @param right    second filter (required)
     * @param others   others filters (optional)
     * @return composite filter
     */
    public static CompositeFilter of(String operator, Filter left, Filter right, Filter... others) {
        CompositeFilter filter = new CompositeFilter(operator);
        filter.filters.add(left);
        filter.filters.add(right);
        if (others != null) {
            filter.filters.addAll(Arrays.stream(others).toList());
        }
        return filter;
    }

    @Override
    public void accept(QueryFilterVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String getKey() {
        return operator;
    }

}
