package fr.ouestfrance.querydsl.postgrest;

import lombok.RequiredArgsConstructor;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.json.JsonMapper;

import java.lang.reflect.Type;
import java.util.List;

/**
 * Translate raw json bodies to objects
 */
@RequiredArgsConstructor
public class JsonResponseReader {

    private final JsonMapper mapper;

    /**
     * Reader with default json mapper
     */
    public JsonResponseReader() {
        this(JsonMapper.builder().build());
    }

    /**
     * Read a json array
     *
     * @param body  raw body
     * @param clazz class of elements
     * @param <T>   type of elements
     * @return list of T, empty if body is empty
     */
    public <T> List<T> readList(byte[] body, Class<T> clazz) {
        if (isEmpty(body)) {
            return List.of();
        }
        JavaType type = mapper.getTypeFactory().constructCollectionType(List.class, clazz);
        List<T> list = mapper.readValue(body, type);
        // a json "null" body is an empty result, as with the webclient adapter
        return list == null ? List.of() : list;
    }

    /**
     * Read a json value
     *
     * @param body raw body
     * @param type expected type
     * @param <V>  type of value
     * @return value, null if body is empty
     */
    public <V> V read(byte[] body, Type type) {
        if (isEmpty(body)) {
            return null;
        }
        return mapper.readValue(body, mapper.getTypeFactory().constructType(type));
    }

    private static boolean isEmpty(byte[] body) {
        return body == null || body.length == 0;
    }
}
