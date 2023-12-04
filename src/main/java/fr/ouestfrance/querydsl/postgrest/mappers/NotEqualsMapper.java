package fr.ouestfrance.querydsl.postgrest.mappers;

import fr.ouestfrance.querydsl.FilterOperation;
import fr.ouestfrance.querydsl.postgrest.model.Filter;
import fr.ouestfrance.querydsl.postgrest.model.impl.QueryFilter;


/**
 * Concrete mapping for notEquals
 */
public class NotEqualsMapper extends AbstractMapper {

    @Override
    public Filter getFilter(String field, Object value) {
        return QueryFilter.of(field, Operators.NOT_EQUALS, value);
    }


    @Override
    public FilterOperation operation() {
        return FilterOperation.NEQ;
    }
}
