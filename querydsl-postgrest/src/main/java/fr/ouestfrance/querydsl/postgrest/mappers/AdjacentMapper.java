package fr.ouestfrance.querydsl.postgrest.mappers;

import fr.ouestfrance.querydsl.FilterOperation;
import fr.ouestfrance.querydsl.postgrest.PostgrestFilterOperation;

/**
 * Concrete mapping for is adjacent to
 */
public class AdjacentMapper extends AbstractRangeComparisonMapper {

    @Override
    protected String operator() {
        return Operators.ADJACENT;
    }

    @Override
    public Class<? extends FilterOperation> operation() {
        return PostgrestFilterOperation.ADJ.class;
    }
}
