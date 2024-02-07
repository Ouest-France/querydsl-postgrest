package fr.ouestfrance.querydsl.postgrest.model.impl;

import fr.ouestfrance.querydsl.postgrest.builders.FilterVisitor;
import fr.ouestfrance.querydsl.postgrest.builders.QueryFilterVisitor;
import fr.ouestfrance.querydsl.postgrest.model.Filter;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Select filter allow to describe a selection
 */
@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class CountFilter implements Filter, FilterVisitor {

    /**
     * Default query param key for selection
     */
    private static final String KEY_PARAMETER = "select";

    /**
     * Create select filter from embedded resources
     *
     * @return select filter
     */
    public static Filter of() {
        return new CountFilter();
    }

    @Override
    public void accept(QueryFilterVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String getKey() {
        return KEY_PARAMETER;
    }

    public String getMethod() {
        return "count()";
    }
}
