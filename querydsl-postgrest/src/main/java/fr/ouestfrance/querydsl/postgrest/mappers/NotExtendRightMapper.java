package fr.ouestfrance.querydsl.postgrest.mappers;

import fr.ouestfrance.querydsl.FilterOperation;
import fr.ouestfrance.querydsl.postgrest.PostgrestFilterOperation;

/**
 * Concrete mapping for does not extend to the right of
 */
public class NotExtendRightMapper extends AbstractRangeComparisonMapper {

    @Override
    protected String operator() {
        return Operators.NOT_EXTEND_RIGHT;
    }

    @Override
    public Class<? extends FilterOperation> operation() {
        return PostgrestFilterOperation.NXR.class;
    }
}
