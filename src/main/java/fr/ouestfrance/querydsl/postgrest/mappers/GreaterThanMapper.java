package fr.ouestfrance.querydsl.postgrest.mappers;

import fr.ouestfrance.querydsl.FilterOperation;
import fr.ouestfrance.querydsl.postgrest.model.Filter;
import fr.ouestfrance.querydsl.postgrest.model.impl.QueryFilter;

/**
 * Concrete mapping for greaterThan
 */
public class GreaterThanMapper extends AbstractMapper {

    @Override
    public Filter getFilter(String field, Object value) {
        return QueryFilter.of(field, Operators.GREATER_THAN, value);
    }


    @Override
    public FilterOperation operation() {
        return FilterOperation.GT;
    }
}
