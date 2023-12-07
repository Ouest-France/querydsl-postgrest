package fr.ouestfrance.querydsl.postgrest.criterias;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Criteria {

    public static Object byId(Comparable<?> id) {
        return new ById(id);
    }

    public static Object byIds(Comparable<?>... id) {
        return new ByIds(List.of(id));
    }
}
