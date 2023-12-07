package fr.ouestfrance.querydsl.postgrest.model;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * PostgREST honors the Prefer HTTP header specified on RFC 7240.
 * It allows clients to specify required and optional behaviors for their requests.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Prefer {

    /**
     * Prefer Header key
     */
    public static final String HEADER = "Prefer";


    /**
     * Return Representation
     * The return preference can be used to obtain information about affected resource when it’s inserted, updated or deleted.
     * This helps avoid a subsequent GET request.
     */
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Return {
        /**
         * With Prefer: return=minimal, no response body will be returned. This is the default mode for all write requests.
         */
        public static final String MINIMAL = "return=minimal";
        /**
         * Headers Only
         * If the table has a primary key, the response can contain a Location header describing where to find the new object
         * by including the header Prefer: return=headers-only in the request.
         * Make sure that the table is not write-only,
         * otherwise constructing the Location header will cause a permission error.
         */
        public static final String HEADERS_ONLY = "return=headers-only";
        /**
         * On the other end of the spectrum you can get the full created object back in the response to your request
         * by including the header Prefer: return=representation.
         * That way you won’t have to make another HTTP call to discover properties that may have been filled in on the server side
         */
        public static final String REPRESENTATION = "return=representation";

    }


    /**
     * Any missing columns in the payload will be inserted as null values
     * To use the DEFAULT column value instead, use the Prefer: missing=default header.
     */
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Missing {

        /**
         * To use the DEFAULT column value instead of null, use the Prefer: missing=default header.
         */
        public static final String REPRESENTATION = "missing=default";
    }

    /**
     * Upsert resolution
     * You can make an upsert with POST and the Prefer: resolution=merge-duplicates header:
     * By default, upsert operates based on the primary key columns, you must specify all of them
     */
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Resolution {

        /**
         *  You can make an upsert with POST and the Prefer: resolution=merge-duplicates header
         */
        public static final String MERGE_DUPLICATES = "resolution=merge_duplicates";
        /**
         * You can also choose to ignore the duplicates with Prefer: resolution=ignore-duplicates
         */
        public static final String IGNORE_DUPLICATES = "resolution=ignore_duplicates";
    }
}
