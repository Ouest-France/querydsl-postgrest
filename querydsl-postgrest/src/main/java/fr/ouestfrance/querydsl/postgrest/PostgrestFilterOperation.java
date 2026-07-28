package fr.ouestfrance.querydsl.postgrest;

import fr.ouestfrance.querydsl.FilterOperation;
import fr.ouestfrance.querydsl.service.validators.ValidatedBy;
import fr.ouestfrance.querydsl.service.validators.impl.HasRangeValidator;
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

    /**
     * Overlap for Range datatype, e.g. ranges having points in common
     */
    @ValidatedBy(HasRangeValidator.class)
    class OV implements FilterOperation {
    }

    /**
     * Strictly left of for Range datatype
     */
    @ValidatedBy(HasRangeValidator.class)
    class SL implements FilterOperation {
    }

    /**
     * Strictly right of for Range datatype
     */
    @ValidatedBy(HasRangeValidator.class)
    class SR implements FilterOperation {
    }

    /**
     * Does not extend to the right of for Range datatype
     */
    @ValidatedBy(HasRangeValidator.class)
    class NXR implements FilterOperation {
    }

    /**
     * Does not extend to the left of for Range datatype
     */
    @ValidatedBy(HasRangeValidator.class)
    class NXL implements FilterOperation {
    }

    /**
     * Is adjacent to for Range datatype
     */
    @ValidatedBy(HasRangeValidator.class)
    class ADJ implements FilterOperation {
    }
}
