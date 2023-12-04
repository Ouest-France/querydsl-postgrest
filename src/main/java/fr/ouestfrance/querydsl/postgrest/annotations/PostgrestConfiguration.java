package fr.ouestfrance.querydsl.postgrest.annotations;

import org.springframework.stereotype.Repository;

import java.lang.annotation.*;

import static fr.ouestfrance.querydsl.postgrest.annotations.PostgrestConfiguration.CountType.EXACT;

/**
 * PostgresConfiguration annotation allow to create PostgrestRepository with values
 * resource : name of the path on the resource
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
     * Specify the count strategy
     *
     * @return type of count strategy
     */
    CountType countStrategy() default EXACT;

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
