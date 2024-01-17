package fr.ouestfrance.querydsl.postgrest.model.impl;


import fr.ouestfrance.querydsl.postgrest.builders.FilterVisitor;
import fr.ouestfrance.querydsl.postgrest.builders.QueryFilterVisitor;
import fr.ouestfrance.querydsl.postgrest.model.Filter;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

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
public class CompositeFilter implements Filter, FilterVisitor {

    /**
     * Operator function (allowed and/or)
     */
    private final String operator;

    /**
     * Alias on the composite filter
     */
    private String alias;
    /**
     * Filters list
     */
    @Getter
    private final List<Filter> filters;


    /**
     * Composite filter constructor
     *
     * @param operator operator of the filter
     * @param filters  list of sub filters
     */
    private CompositeFilter(String operator, List<Filter> filters) {

        this.operator = operator;
        this.filters = filters;

        // Check for alias
        Optional<Filter> first = filters.stream().filter(QueryFilter.class::isInstance).findFirst();
        first.filter(x -> x.getKey().contains(".")).ifPresent(x -> {
            // There is an alias
            int aliasIndex = x.getKey().lastIndexOf(".");
            this.alias = x.getKey().substring(0, aliasIndex);

            List<Filter> newFilterList = filters.stream().map(filter -> {
                if (filter instanceof QueryFilter query) {
                    return QueryFilter.of(query.getKey().substring(aliasIndex + 1), query.getOperator(), query.getValue());
                }
                if (filter instanceof CompositeFilter compositeFilter && this.alias.equals(compositeFilter.alias)) {
                    compositeFilter.alias = null;
                }


                return filter;
            }).toList();

            filters.clear();
            filters.addAll(newFilterList);

        });
    }

    /**
     * Static method to create composite filter
     *
     * @param operator type of composite operator (or, and, not)
     * @param left     first filter (required)
     * @param others   others filters (optional)
     * @return composite filter
     */
    public static Filter of(String operator, Filter left, Filter... others) {
        List<Filter> filters = new ArrayList<>();
        filters.add(left);
        if (others != null) {
            filters.addAll(Arrays.stream(others).toList());
        }
        return CompositeFilter.of(operator, filters);
    }

    /**
     * Create a composite filter from a list of filters and an operator
     *
     * @param operator logical operator
     * @param filters  list of filter
     * @return composite filter
     */
    public static Filter of(String operator, List<Filter> filters) {
        return new CompositeFilter(operator, filters);
    }

    @Override
    public void accept(QueryFilterVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String getKey() {
        return alias != null ? alias + "." + operator : operator;
    }

}
