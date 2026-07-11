package fr.ouestfrance.querydsl.postgrest.mappers;

import fr.ouestfrance.querydsl.FilterOperation;
import fr.ouestfrance.querydsl.postgrest.PostgrestFilterOperation;

/**
 * Concrete mapping for strictly left of
 */
public class StrictlyLeftMapper extends AbstractRangeComparisonMapper {

    @Override
    protected String operator() {
        return Operators.STRICTLY_LEFT;
    }

    @Override
    public Class<? extends FilterOperation> operation() {
        return PostgrestFilterOperation.SL.class;
    }
}
