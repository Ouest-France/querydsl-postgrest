package fr.ouestfrance.querydsl.postgrest.builders;

import fr.ouestfrance.querydsl.postgrest.model.Filter;
import fr.ouestfrance.querydsl.postgrest.model.Sort;
import fr.ouestfrance.querydsl.postgrest.model.impl.CompositeFilter;
import fr.ouestfrance.querydsl.postgrest.model.impl.OrderFilter;
import fr.ouestfrance.querydsl.postgrest.model.impl.SelectFilter;
import fr.ouestfrance.querydsl.postgrest.model.impl.SimpleFilter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * QueryVisitor to allow building queries
 */
public final class QueryFilterVisitor {

    private static final String DOT = ".";
    private static final String OPEN_PARENTHESIS = "(";
    private static final String CLOSE_PARENTHESIS = ")";
    private static final String COMA = ",";
    private static final String EMPTY_STRING = "";

    /**
     * StringBuilder
     */
    private final StringBuilder builder = new StringBuilder();

    /**
     * Transform SimpleFilter to Query
     *
     * @param filter simple filter
     */
    public void visit(SimpleFilter filter) {
        builder
                .append(filter.getOperator())
                .append(QueryFilterVisitor.DOT)
                .append(filter.getValue());
    }

    /**
     * Transform OrderFilter to Query
     *
     * @param filter order filter
     */
    public void visit(OrderFilter filter) {
        builder.append(filter.getSort().getOrders().stream().map(
                x -> x.getProperty()
                        + (Sort.Direction.ASC.equals(x.getDirection()) ? EMPTY_STRING : DOT + "desc")
                        + (switch (x.getNullHandling()) {
                    case NATIVE -> EMPTY_STRING;
                    case NULLS_FIRST -> DOT + "nullsfirst";
                    case NULLS_LAST -> DOT + "nullslast";
                })).collect(Collectors.joining(COMA)));
    }

    /**
     * Transform a Select filter to Query
     *
     * @param filter select filter
     */
    public void visit(SelectFilter filter) {
        builder.append("*,").append(Arrays.stream(filter.getEmbedded()).map(x -> x + "(*)")
                .collect(Collectors.joining(COMA)));
    }

    /**
     * Transform a Composite filter to Query
     *
     * @param filter composite filter
     */
    public void visit(CompositeFilter filter) {
        builder.append(OPEN_PARENTHESIS);
        Filter lastElement = getLastElement(filter.getFilters());
        filter.getFilters().forEach(item -> {
            builder.append(item.getKey()).append(DOT);
            item.accept(this);
            if (!item.equals(lastElement)) {
                builder.append(COMA);
            }
        });
        builder.append(CLOSE_PARENTHESIS);
    }

    /**
     * Return the last element of a list
     *
     * @param filters list of filters
     * @return last element
     */
    private Filter getLastElement(List<Filter> filters) {
        return filters.get(filters.size() - 1);
    }

    /**
     * Return string representation of the queryString
     * @return string representation of the queryString
     */
    public String getValue() {
        return builder.toString();
    }
}
