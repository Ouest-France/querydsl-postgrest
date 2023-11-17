package fr.ouestfrance.querydsl.postgrest.model;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Header keys used in postgrest
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Headers {
    /**
     * Header rangeUnit.
     */
    public static final String RANGE_UNIT = "Range-Unit";
    /**
     * Header range
     */
    public static final String RANGE = "Range";

    /**
     * Header prefers
     */
    public static final String PREFER = "Prefer";

    /**
     * Header content-Range
     */
    public static final String CONTENT_RANGE = "Content-Range";
}
