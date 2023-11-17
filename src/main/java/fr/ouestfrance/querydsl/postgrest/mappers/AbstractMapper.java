package fr.ouestfrance.querydsl.postgrest.mappers;

import fr.ouestfrance.querydsl.model.FilterFieldInfoModel;
import fr.ouestfrance.querydsl.postgrest.model.Filter;
import fr.ouestfrance.querydsl.postgrest.model.impl.CompositeFilter;
import fr.ouestfrance.querydsl.postgrest.model.impl.SimpleFilter;
import fr.ouestfrance.querydsl.service.ext.Mapper;

/**
 * Abstract mapper to simplify sub mapping
 */
public abstract class AbstractMapper implements Mapper<Filter> {



    @Override
    public Filter map(FilterFieldInfoModel filterField, Object data) {
        Filter filter = getFilter(filterField.getKey(), data);
        if (filterField.isOrNull()) {
            return CompositeFilter.of(Operators.OR,
                    filter,
                    SimpleFilter.of(filterField.getKey(), Operators.IS, null)
            );
        }
        return filter;
    }

    /**
     * Get concrete filter for mapper
     * @param field fieldName
     * @param value value to bind
     * @return filter
     */

    public abstract Filter getFilter(String field, Object value);
}
