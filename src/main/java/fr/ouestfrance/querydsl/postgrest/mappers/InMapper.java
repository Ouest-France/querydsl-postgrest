package fr.ouestfrance.querydsl.postgrest.mappers;

import java.util.Collection;
import java.util.stream.Collectors;

import fr.ouestfrance.querydsl.FilterOperation;
import fr.ouestfrance.querydsl.postgrest.model.Filter;
import fr.ouestfrance.querydsl.postgrest.model.exceptions.PostgrestRequestException;
import fr.ouestfrance.querydsl.postgrest.model.impl.QueryFilter;


/**
 * Concrete mapping for in list
 */
public class InMapper extends AbstractMapper {

    @Override
    public Filter getFilter(String field, Object value) {
        if (value instanceof Collection<?> col) {
            return QueryFilter.of(field, Operators.IN, "(" + col.stream().map(Object::toString).collect(Collectors.joining(",")) + ")");
        }
        throw new PostgrestRequestException("Filter " + operation() + " should be on Collection type but was " + value.getClass().getSimpleName());
    }


    @Override
    public Class<? extends FilterOperation> operation() {
        return FilterOperation.IN.class;
    }
}
