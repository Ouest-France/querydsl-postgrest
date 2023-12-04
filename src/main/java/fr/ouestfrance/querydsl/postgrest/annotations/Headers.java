package fr.ouestfrance.querydsl.postgrest.annotations;

import java.lang.annotation.*;

/**
 * Header annotation to specify key/values data
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
public @interface Headers {

    /**
     * List of header
     * @return headers
     */
    Header[] value() default {};
}
