package fr.ouestfrance.querydsl.postgrest;

import fr.ouestfrance.querydsl.FilterOperation;
import fr.ouestfrance.querydsl.service.validators.ValidatedBy;
import fr.ouestfrance.querydsl.service.validators.impl.StringValidator;

/**
 * Accessors of specific filter operations (extend FilterOperation)
 */
public interface PostgrestFilterOperation {
    /**
     * Case-insensitive like
     */
    @ValidatedBy(StringValidator.class)
    class ILIKE implements FilterOperation {
    }

    /**
     * Contains for JSON/Range datatype
     */
    @ValidatedBy(StringValidator.class)
    class CS implements FilterOperation {
    }

    /**
     * Contained for JSON/Range datatype
     */
    @ValidatedBy(StringValidator.class)
    class CD implements FilterOperation {
    }
}
