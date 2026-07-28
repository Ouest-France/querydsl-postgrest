package fr.ouestfrance.querydsl.postgrest.mappers;

import fr.ouestfrance.querydsl.postgrest.model.Filter;
import fr.ouestfrance.querydsl.postgrest.model.exceptions.PostgrestRequestException;
import fr.ouestfrance.querydsl.postgrest.model.impl.QueryFilter;
import fr.ouestfrance.querydsl.service.ext.HasRange;

/**
 * Base mapper for range to range comparison operators (ov, sl, sr, nxr, nxl, adj)
 * Formats a {@link HasRange} value into a PostgREST range literal, e.g. {@code [1,10)}
 */
public abstract class AbstractRangeComparisonMapper extends AbstractMapper {

    @Override
    public Filter getFilter(String field, Object value) {
        if (value instanceof HasRange<?> hasRange) {
            return QueryFilter.of(field, operator(), toRangeLiteral(hasRange));
        }
        throw new PostgrestRequestException("Filter " + operation() + " should be on HasRange type but was " + value.getClass().getSimpleName());
    }

    private String toRangeLiteral(HasRange<?> range) {
        String lower = range.getLower() != null ? range.getLower().toString() : "";
        String upper = range.getUpper() != null ? range.getUpper().toString() : "";
        return (range.isLowerInclusive() ? "[" : "(") + lower + "," + upper + (range.isUpperInclusive() ? "]" : ")");
    }

    /**
     * Postgrest abbreviation of the operator (ov, sl, sr, nxr, nxl, adj)
     *
     * @return operator abbreviation
     */
    protected abstract String operator();
}
