package fr.ouestfrance.querydsl.postgrest.mappers;

import fr.ouestfrance.querydsl.FilterOperation;
import fr.ouestfrance.querydsl.postgrest.model.Filter;
import fr.ouestfrance.querydsl.postgrest.model.impl.QueryFilter;

/**
 * Concrete mapping for lessThanEquals
 */
public class LessThanEqualsMapper extends AbstractMapper {

    @Override
    public Filter getFilter(String field, Object value) {
        return QueryFilter.of(field, Operators.LESS_THAN_EQUALS, value);
    }


    @Override
    public Class<? extends FilterOperation> operation() {
        return FilterOperation.LTE.class;
    }
}
