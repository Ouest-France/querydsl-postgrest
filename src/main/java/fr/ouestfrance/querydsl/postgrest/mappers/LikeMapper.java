package fr.ouestfrance.querydsl.postgrest.mappers;

import fr.ouestfrance.querydsl.FilterOperation;
import fr.ouestfrance.querydsl.postgrest.model.Filter;
import fr.ouestfrance.querydsl.postgrest.model.impl.SimpleFilter;


/**
 * Concrete mapping for like
 */
public class LikeMapper extends AbstractMapper {

    @Override
    public Filter getFilter(String field, Object value) {
        return SimpleFilter.of(field, Operators.LIKE, "{" + value + "}");
    }


    @Override
    public FilterOperation operation() {
        return FilterOperation.LIKE;
    }
}
