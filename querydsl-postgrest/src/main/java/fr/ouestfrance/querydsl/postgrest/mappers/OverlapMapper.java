package fr.ouestfrance.querydsl.postgrest.mappers;

import fr.ouestfrance.querydsl.FilterOperation;
import fr.ouestfrance.querydsl.postgrest.PostgrestFilterOperation;

/**
 * Concrete mapping for overlap (ranges having points in common)
 */
public class OverlapMapper extends AbstractRangeComparisonMapper {

    @Override
    protected String operator() {
        return Operators.OVERLAP;
    }

    @Override
    public Class<? extends FilterOperation> operation() {
        return PostgrestFilterOperation.OV.class;
    }
}
