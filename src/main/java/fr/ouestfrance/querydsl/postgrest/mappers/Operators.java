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
    public static final String EQUALS = "eq";
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
    public static final String LIKE = "cs";
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
}
