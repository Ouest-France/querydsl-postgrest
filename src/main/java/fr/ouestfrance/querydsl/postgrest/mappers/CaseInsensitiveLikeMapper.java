package fr.ouestfrance.querydsl.postgrest.mappers;

import fr.ouestfrance.querydsl.FilterOperation;
import fr.ouestfrance.querydsl.postgrest.PostgrestFilterOperation;
import fr.ouestfrance.querydsl.postgrest.model.Filter;
import fr.ouestfrance.querydsl.postgrest.model.impl.QueryFilter;


/**
 * Concrete mapping for like
 */
public class CaseInsensitiveLikeMapper extends AbstractMapper {

    @Override
    public Filter getFilter(String field, Object value) {
        return QueryFilter.of(field, Operators.ILIKE, value);
    }


    @Override
    public Class<? extends FilterOperation> operation() {
        return PostgrestFilterOperation.ILIKE.class;
    }
}
