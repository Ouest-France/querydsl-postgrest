package fr.ouestfrance.querydsl.postgrest.mappers;

import fr.ouestfrance.querydsl.model.SimpleFilter;
import fr.ouestfrance.querydsl.postgrest.model.Filter;
import fr.ouestfrance.querydsl.postgrest.model.impl.CompositeFilter;
import fr.ouestfrance.querydsl.postgrest.model.impl.QueryFilter;
import fr.ouestfrance.querydsl.service.ext.Mapper;

/**
 * Abstract mapper to simplify sub mapping
 */
public abstract class AbstractMapper implements Mapper<Filter> {


    @Override
    public Filter map(SimpleFilter filterField, Object data) {
        Filter filter = getFilter(filterField.key(), data);
        if (filterField.orNull()) {
            return CompositeFilter.of(Operators.OR,
                    filter,
                    QueryFilter.of(filterField.key(), Operators.IS, null)
            );
        }
        return filter;
    }

    /**
     * Get concrete filter for mapper
     *
     * @param field fieldName
     * @param value value to bind
     * @return filter
     */

    public abstract Filter getFilter(String field, Object value);
}
