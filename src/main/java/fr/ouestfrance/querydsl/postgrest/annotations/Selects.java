package fr.ouestfrance.querydsl.postgrest.annotations;

import java.lang.annotation.*;

/**
 * Iterable annotation for select
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface Selects {
    /**
     * List of select, using value allow to avoid attribute name
     *
     * @return selects
     */
    Select[] value() default {};
}
