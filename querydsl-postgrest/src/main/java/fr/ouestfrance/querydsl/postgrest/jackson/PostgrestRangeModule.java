package fr.ouestfrance.querydsl.postgrest.jackson;

import fr.ouestfrance.querydsl.postgrest.model.Range;
import tools.jackson.databind.module.SimpleModule;

/**
 * Jackson module binding {@link Range} to the PostgREST range format. It is discovered through
 * {@code ServiceLoader}, so registering it by hand is only needed on a mapper built without
 * {@code findAndAddModules()} : see the
 * {@link fr.ouestfrance.querydsl.postgrest.jackson package documentation}.
 *
 * @see RangeSerializer
 * @see RangeDeserializer
 */
public class PostgrestRangeModule extends SimpleModule {

    /**
     * Registers {@link RangeSerializer} and {@link RangeDeserializer} on {@link Range}
     */
    @SuppressWarnings("unchecked") // Range<T> is generic but Range.class only exists as a raw class.
    public PostgrestRangeModule() {
        super(PostgrestRangeModule.class.getName());
        Class<Range<Object>> rangeType = (Class<Range<Object>>) (Class<?>) Range.class;
        addDeserializer(rangeType, new RangeDeserializer());
        addSerializer(rangeType, new RangeSerializer());
    }
}
