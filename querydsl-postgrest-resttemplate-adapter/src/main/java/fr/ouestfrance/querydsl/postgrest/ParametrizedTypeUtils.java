package fr.ouestfrance.querydsl.postgrest;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.reflect.TypeUtils;
import org.springframework.core.ParameterizedTypeReference;

import java.util.List;

/**
 * Type Utilities
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ParametrizedTypeUtils {

    /**
     * Create parametrized type of List of T
     *
     * @param clazz class of T
     * @param <T>   type of parametrized list
     * @return parametrized type
     */
    public static <T> ParameterizedTypeReference<List<T>> listRef(Class<T> clazz) {
        return ParameterizedTypeReference.forType(TypeUtils.parameterize(List.class, clazz));
    }
}
