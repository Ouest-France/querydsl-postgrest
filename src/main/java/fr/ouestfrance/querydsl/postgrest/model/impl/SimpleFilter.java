package fr.ouestfrance.querydsl.postgrest.model.impl;

import fr.ouestfrance.querydsl.postgrest.builders.FilterVisitor;
import fr.ouestfrance.querydsl.postgrest.builders.QueryFilterVisitor;
import fr.ouestfrance.querydsl.postgrest.model.Filter;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Simple filter allow to describe a single param in the query string
 */
@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class SimpleFilter implements Filter, FilterVisitor {

    /**
     * Field name
     */
    private final String key;
    /**
     * operator (eq, ne, gt, ...).
     */
    private final String operator;
    /**
     * Value
     */
    private final Object value;

    /**
     * Static method to build a simple filter
     *
     * @param fieldName name of the query param
     * @param operator  operator of the query param
     * @param value     value of the query param
     * @return Simple filter
     */
    public static Filter of(String fieldName, String operator, Object value) {
        return new SimpleFilter(fieldName, operator, value);
    }

    @Override
    public void accept(QueryFilterVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String getKey() {
        return key;
    }
}
