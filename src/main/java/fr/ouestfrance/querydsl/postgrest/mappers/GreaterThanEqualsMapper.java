package fr.ouestfrance.querydsl.postgrest.mappers;

import fr.ouestfrance.querydsl.postgrest.model.Filter;
import fr.ouestfrance.querydsl.postgrest.model.impl.QueryFilter;
import fr.ouestfrance.querydsl.FilterOperation;


/**
 * Concrete mapping for greaterThanEquals
 */
public class GreaterThanEqualsMapper extends AbstractMapper {

    @Override
    public Filter getFilter(String field, Object value) {
        return QueryFilter.of(field, Operators.GREATER_THAN_EQUALS, value);
    }


    @Override
    public FilterOperation operation() {
        return FilterOperation.GTE;
    }
}
