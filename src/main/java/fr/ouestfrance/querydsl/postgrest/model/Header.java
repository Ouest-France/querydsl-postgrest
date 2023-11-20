package fr.ouestfrance.querydsl.postgrest.model;

import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Header annotation to specify key/values data
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface Header {

    /**
     * Key of the header
     * @return key
     */
    String key();

    /**
     * Values of the header
     * @return values of the header
     */
    String[] value();
}
