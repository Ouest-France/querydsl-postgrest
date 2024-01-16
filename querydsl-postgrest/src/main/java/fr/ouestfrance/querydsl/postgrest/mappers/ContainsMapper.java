package fr.ouestfrance.querydsl.postgrest.mappers;

import fr.ouestfrance.querydsl.FilterOperation;
import fr.ouestfrance.querydsl.postgrest.PostgrestFilterOperation;
import fr.ouestfrance.querydsl.postgrest.model.Filter;
import fr.ouestfrance.querydsl.postgrest.model.impl.QueryFilter;


/**
 * Concrete mapping for equals
 */
public class ContainsMapper extends AbstractMapper {

    @Override
    public Filter getFilter(String field, Object value) {
        return QueryFilter.of(field, Operators.CONTAINS, value);
    }

    @Override
    public Class<? extends FilterOperation> operation() {
        return PostgrestFilterOperation.CS.class;
    }
}
