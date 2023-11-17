package fr.ouestfrance.querydsl.postgrest;

import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.function.ServerRequest;

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
     * Specify default headers for deletion
     */
    String[] deleteHeaders() default {};


    /**
     * Specify default headers for upsert
     */
    String[] upsertHeaders() default {};
}
