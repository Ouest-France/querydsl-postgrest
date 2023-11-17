package fr.ouestfrance.querydsl.postgrest.mappers;

import fr.ouestfrance.querydsl.FilterOperation;
import fr.ouestfrance.querydsl.postgrest.model.Filter;
import fr.ouestfrance.querydsl.postgrest.model.exceptions.PostgrestRequestException;
import fr.ouestfrance.querydsl.postgrest.model.impl.SimpleFilter;

import java.util.Collection;
import java.util.stream.Collectors;


/**
 * Concrete mapping for in list
 */
public class InMapper extends AbstractMapper {

    @Override
    public Filter getFilter(String field, Object value) {
        if (value instanceof Collection<?> col) {
            return SimpleFilter.of(field, Operators.IN, "(" + col.stream().map(Object::toString).collect(Collectors.joining(",")) + ")");
        }
        throw new PostgrestRequestException("Filter " + operation() + " should be on Collection type but was " + value.getClass().getSimpleName());
    }


    @Override
    public FilterOperation operation() {
        return FilterOperation.IN;
    }
}
