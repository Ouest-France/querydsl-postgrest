package fr.ouestfrance.querydsl.postgrest.model.impl;

import fr.ouestfrance.querydsl.postgrest.builders.FilterVisitor;
import fr.ouestfrance.querydsl.postgrest.builders.QueryFilterVisitor;
import fr.ouestfrance.querydsl.postgrest.model.Filter;
import fr.ouestfrance.querydsl.postgrest.model.Sort;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Limit filter allows to describe simple limit search
 */
@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class LimitFilter implements Filter, FilterVisitor {

    private static final String KEY_PARAMETER = "limit";
    private final int limit;

    /**
     * Static method to create filter from limit object
     *
     * @param limit limit object
     * @return filter
     */
    public static Filter of(int limit) {
        return new LimitFilter(limit);
    }

    @Override
    public void accept(QueryFilterVisitor visitor) {
        visitor.visit(this);
    }
    
    @Override
    public String getKey() {
        return KEY_PARAMETER;
    }

}
