package fr.ouestfrance.querydsl.postgrest.model.impl;

import fr.ouestfrance.querydsl.postgrest.builders.FilterVisitor;
import fr.ouestfrance.querydsl.postgrest.builders.QueryFilterVisitor;
import fr.ouestfrance.querydsl.postgrest.model.Filter;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

/**
 * Select filter allow to describe a selection
 */
@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class SelectFilter implements Filter, FilterVisitor {

    /**
     * Default query param key for selection
     */
    private static final String KEY_PARAMETER = "select";
    /**
     * alias
     */
    private final List<Attribute> selectAttributes;

    /**
     * Create select filter from embedded resources
     *
     * @param selectAttributes selectAttributes of the selection (can be empty)
     * @return select filter
     */
    public static Filter of(List<Attribute> selectAttributes) {
        return new SelectFilter(selectAttributes);
    }

    @Override
    public void accept(QueryFilterVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String getKey() {
        return KEY_PARAMETER;
    }


    /**
     * Attribute name
     */
    @Getter
    @RequiredArgsConstructor
    public static class Attribute {
        /**
         * alias
         */
        private final String alias;
        /**
         * value selected
         */
        private final String value;

    }
}
