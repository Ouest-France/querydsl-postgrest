package fr.ouestfrance.querydsl.postgrest.mappers;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Operators available for postgrest
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Operators {
    /**
     * Equals operation
     */
    public static final String EQUALS_TO = "eq";
    /**
     * Greater than or Equals operation
     */
    public static final String GREATER_THAN_EQUALS = "gte";
    /**
     * Greater than operation
     */
    public static final String GREATER_THAN = "gt";
    /**
     * In operation
     */
    public static final String IN = "in";
    /**
     * Less than or Equals operation
     */
    public static final String LESS_THAN_EQUALS = "lte";
    /**
     * Less than operation
     */
    public static final String LESS_THAN = "lt";
    /**
     * Like operation
     */
    public static final String LIKE = "like";
    /**
     * Case-insensitive Like operation
     */
    public static final String ILIKE = "ilike";
    /**
     * Not equals operation
     */
    public static final String NOT_EQUALS = "neq";
    /**
     * Not in operation
     */
    public static final String NOT_IN = "not.in";
    /**
     * or operation
     */
    public static final String OR = "or";
    /**
     * is operation
     */
    public static final String IS = "is";
    /**
     * is not operation
     */
    public static final String IS_NOT = "not.is";
    /**
     * Contains operation for JSON/Range datatype
     */
    public static final String CONTAINS = "cs";
    /**
     * Contained operation for JSON/Range datatype
     */
    public static final String CONTAINED = "cd";
    /**
     * Overlap operation for Range/Array datatype (have points in common)
     */
    public static final String OVERLAP = "ov";
    /**
     * Strictly left of operation for Range datatype
     */
    public static final String STRICTLY_LEFT = "sl";
    /**
     * Strictly right of operation for Range datatype
     */
    public static final String STRICTLY_RIGHT = "sr";
    /**
     * Does not extend to the right of operation for Range datatype
     */
    public static final String NOT_EXTEND_RIGHT = "nxr";
    /**
     * Does not extend to the left of operation for Range datatype
     */
    public static final String NOT_EXTEND_LEFT = "nxl";
    /**
     * Is adjacent to operation for Range datatype
     */
    public static final String ADJACENT = "adj";
}
