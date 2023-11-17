package fr.ouestfrance.querydsl.postgrest.mappers;

import fr.ouestfrance.querydsl.FilterOperation;
import fr.ouestfrance.querydsl.postgrest.model.Filter;
import fr.ouestfrance.querydsl.postgrest.model.impl.SimpleFilter;


/**
 * Concrete mapping for equals
 */
public class EqualsMapper extends AbstractMapper {

    @Override
    public Filter getFilter(String field, Object value) {
        return SimpleFilter.of(field, Operators.EQUALS, value);
    }
    @Override
    public FilterOperation operation() {
        return FilterOperation.EQ;
    }
}
