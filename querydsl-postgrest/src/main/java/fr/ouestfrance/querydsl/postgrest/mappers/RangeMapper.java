package fr.ouestfrance.querydsl.postgrest.mappers;

import fr.ouestfrance.querydsl.FilterOperation;
import fr.ouestfrance.querydsl.postgrest.model.Filter;
import fr.ouestfrance.querydsl.postgrest.model.exceptions.PostgrestRequestException;
import fr.ouestfrance.querydsl.postgrest.model.impl.CompositeFilter;
import fr.ouestfrance.querydsl.postgrest.model.impl.QueryFilter;
import fr.ouestfrance.querydsl.service.ext.HasRange;

import java.util.ArrayList;
import java.util.List;


/**
 * Concrete mapping for equals
 */
public class RangeMapper extends AbstractMapper {

    @Override
    public Filter getFilter(String field, Object value) {
        if (value instanceof HasRange<?> hasRange) {
            Object lowerBound = hasRange.getLower();
            Object upperBound = hasRange.getUpper();

            if (lowerBound != null && lowerBound.equals(upperBound)) {
                return QueryFilter.of(field, Operators.EQUALS_TO, lowerBound);
            }

            List<Filter> filterList = new ArrayList<>();
            // If there is lowerBound, then we use GREATER_THAN_EQUALS or GREATER_THAN (depending on inclusivity)
            if (lowerBound != null) {
                filterList.add(leftFilter(field, lowerBound, hasRange.isUpperInclusive()));
            }

            // If there is upperBound, then we use LESS_THAN_EQUALS or LESS_THAN (depending on inclusivity)
            if (upperBound != null) {
                filterList.add(rightFilter(field, upperBound, hasRange.isLowerInclusive()));
            }

            // Return composite if there is both upper and lower, otherwise return the single filter
            return filterList.size() == 1 ? filterList.get(0) : CompositeFilter.of("and", filterList);
        }
        // If the value is not a HasRange, we throw an exception
        throw new PostgrestRequestException("Filter " + operation() + " should be on HasRange type but was " + value.getClass().getSimpleName());
    }

    /**
     * Create a filter for the left bound
     * @param field name of the field
     * @param lowerBound value of the lower bound
     * @param isInclusive if the lower bound is inclusive
     * @return Simple filter defining xx is greater than (or equals) to lowerBound
     */
    private Filter leftFilter(String field, Object lowerBound, boolean isInclusive) {
        return QueryFilter.of(field, isInclusive ? Operators.GREATER_THAN_EQUALS : Operators.GREATER_THAN, lowerBound);
    }

    /**
     * Create a filter for the right bound
     * @param field name of the field
     * @param upperBound value of the upper bound
     * @param isInclusive if the upper bound is inclusive
     * @return Simple filter defining xx is less than (or equals) to upperBound
     */
    private Filter rightFilter(String field, Object upperBound, boolean isInclusive) {
        return QueryFilter.of(field, isInclusive ? Operators.LESS_THAN_EQUALS : Operators.LESS_THAN, upperBound);
    }

    @Override
    public Class<? extends FilterOperation> operation() {
        return FilterOperation.BETWEEN.class;
    }
}
