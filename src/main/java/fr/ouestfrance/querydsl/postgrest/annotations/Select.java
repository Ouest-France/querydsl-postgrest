package fr.ouestfrance.querydsl.postgrest.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Select annotation allow to add extra join selection for a repository or during a request
 */
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Repeatable(Selects.class)
public @interface Select {
    /**
     * Selection
     * @return string representation of the selection
     */
    String value();

    /**
     * Select the value as a specific alias name
     * @return alias name
     */
    String alias() default "";
}
