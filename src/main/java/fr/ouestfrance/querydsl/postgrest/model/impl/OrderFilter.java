package fr.ouestfrance.querydsl.postgrest.model.impl;

import fr.ouestfrance.querydsl.postgrest.builders.FilterVisitor;
import fr.ouestfrance.querydsl.postgrest.builders.QueryFilterVisitor;
import fr.ouestfrance.querydsl.postgrest.model.Sort;
import fr.ouestfrance.querydsl.postgrest.model.Filter;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Order filter allow to describe a pagination sort
 */
@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class OrderFilter implements Filter, FilterVisitor {

    private static final String KEY_PARAMETER = "order";
    private final Sort sort;

    /**
     * Static method to create filter from sort object
     *
     * @param sort sort object
     * @return filter
     */
    public static Filter of(Sort sort) {
        return new OrderFilter(sort);
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
