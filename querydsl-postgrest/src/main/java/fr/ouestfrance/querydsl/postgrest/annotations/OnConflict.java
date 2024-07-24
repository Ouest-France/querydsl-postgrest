package fr.ouestfrance.querydsl.postgrest.annotations;

import java.lang.annotation.*;

/**
 * OnConflict annotation to specify which columns compose the unique constraint
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
public @interface OnConflict {

    /**
     * Columns names of the unique constraint
     * @return Tab of the Columns names
     */
    String[] columnNames() default {};
}
