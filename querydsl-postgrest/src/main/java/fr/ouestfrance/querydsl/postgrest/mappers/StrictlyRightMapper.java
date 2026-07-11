package fr.ouestfrance.querydsl.postgrest.mappers;

import fr.ouestfrance.querydsl.FilterOperation;
import fr.ouestfrance.querydsl.postgrest.PostgrestFilterOperation;

/**
 * Concrete mapping for strictly right of
 */
public class StrictlyRightMapper extends AbstractRangeComparisonMapper {

    @Override
    protected String operator() {
        return Operators.STRICTLY_RIGHT;
    }

    @Override
    public Class<? extends FilterOperation> operation() {
        return PostgrestFilterOperation.SR.class;
    }
}
