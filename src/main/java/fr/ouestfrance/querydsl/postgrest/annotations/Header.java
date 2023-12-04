package fr.ouestfrance.querydsl.postgrest.annotations;

import java.lang.annotation.*;

/**
 * Header annotation to specify key/values data
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
@Repeatable(Headers.class)
public @interface Header {

    /**
     * Key of the header
     * @return key
     */
    String key() default "";

    /**
     * Values of the header
     * @return values of the header
     */
    String[] value() default "";

    /**
     * Method for the header (default ALL)
     * @return method
     */
    Method[] methods() default {Method.GET, Method.PATCH, Method.UPSERT, Method.DELETE};

    /**
     * List of method
     */
    enum Method{
        /** Http GET*/
        GET,
        /**Http PATCH*/
        PATCH,
        /**Http PUT*/
        UPSERT,
        /**Http DELETE*/
        DELETE
    }
}
