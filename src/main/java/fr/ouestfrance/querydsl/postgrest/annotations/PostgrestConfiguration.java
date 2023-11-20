package fr.ouestfrance.querydsl.postgrest.annotations;

import fr.ouestfrance.querydsl.postgrest.model.Header;
import org.springframework.stereotype.Repository;

import java.lang.annotation.*;

/**
 * PostgresConfiguration annotation allow to create PostgrestRepository with values
 * resource : name of the path on the resource $domainUrl/$resource
 * Example :
 * - $domainUrl : localhost:9000
 * - $resource : posts
 * fullURL : localhost:9000/posts
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Repository
public @interface PostgrestConfiguration {
    /**
     * Resource name
     *
     * @return Resource name
     */
    String resource();

    /**
     * Specify sub resources to embed
     *
     * @return array of sub resources
     */
    String[] embedded() default {};

    /**
     * Specify the count strategy
     *
     * @return type of count strategy
     */
    CountType countStrategy() default CountType.EXACT;

    /**
     * Specify default headers for deletion
     *
     * @return headers to apply to delete
     */
    Header[] deleteHeaders() default {};


    /**
     * Specify default headers for upsert
     *
     * @return headers to apply to upsert
     */
    Header[] upsertHeaders() default {};

    /**
     * Specify default headers for patch
     *
     * @return headers to apply to patch
     */
    Header[] patchHeaders() default {};

    /**
     * Count Type
     */
    enum CountType {
        /**Get the total size of the table*/
        EXACT,
        /**Get a fairly accurate and fast count*/
        PLANNED,
        /**Get an estimated count of the number of elements*/
        ESTIMATED
    }
}
