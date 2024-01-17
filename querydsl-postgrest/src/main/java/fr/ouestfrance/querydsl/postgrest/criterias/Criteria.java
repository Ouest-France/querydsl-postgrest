package fr.ouestfrance.querydsl.postgrest.criterias;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Criterias class
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Criteria {

    /**
     * Allow to generate a simple ById Criteria
     * @param id identifier to search
     * @return criteria object
     */
    public static Object byId(Comparable<?> id) {
        return new ById(id);
    }

    /**
     * Allow to generate a simple ByIds Criteria
     * @param ids list of ids
     * @return criteria object
     */
    public static Object byIds(Comparable<?>... ids) {
        return new ByIds(List.of(ids));
    }
}
