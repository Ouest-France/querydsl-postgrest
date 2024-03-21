package fr.ouestfrance.querydsl.postgrest.mappers;

import fr.ouestfrance.querydsl.FilterOperation;
import fr.ouestfrance.querydsl.postgrest.model.Filter;
import fr.ouestfrance.querydsl.postgrest.model.impl.QueryFilter;

public class IsNullMapper extends AbstractMapper {

    @Override
    public Filter getFilter(String field, Object value) {
        return QueryFilter.of(field, Boolean.TRUE.equals(value) ? Operators.IS : Operators.IS_NOT, null);
    }

    @Override
    public Class<? extends FilterOperation> operation() {
        return FilterOperation.ISNULL.class;
    }
}
