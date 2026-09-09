package fr.ouestfrance.querydsl.postgrest.jackson;

import fr.ouestfrance.querydsl.postgrest.model.Range;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.BeanProperty;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueSerializer;
import tools.jackson.databind.util.TokenBuffer;

/**
 * Serializes a {@link Range} into a PostgREST range literal, the bound formatting being delegated
 * to the serializer registered for the bound type. Reciprocal of {@link RangeDeserializer} : see
 * the {@link fr.ouestfrance.querydsl.postgrest.jackson package documentation} for the produced
 * format.
 *
 * @see RangeDeserializer
 * @see PostgrestRangeModule
 */
public class RangeSerializer extends ValueSerializer<Range<Object>> {

    private final ValueSerializer<Object> contentSerializer;

    /**
     * Creates a non contextual serializer, specialized later by {@link #createContextual}
     */
    public RangeSerializer() {
        this(null);
    }

    private RangeSerializer(ValueSerializer<Object> contentSerializer) {
        this.contentSerializer = contentSerializer;
    }

    /**
     * Resolves {@code T} from the generic type of the field and returns a specialized instance
     * holding the delegate serializer for {@code T}
     *
     * @param ctxt     serialization context
     * @param property property being serialized, may be null
     * @return specialized serializer
     */
    @Override
    public ValueSerializer<?> createContextual(SerializationContext ctxt, BeanProperty property) {
        JavaType wrapperType = property != null ? property.getType() : null;
        if (wrapperType == null || wrapperType.containedTypeCount() == 0) {
            return this;
        }
        ValueSerializer<Object> resolvedContentSerializer =
                ctxt.findContentValueSerializer(wrapperType.containedType(0), property);
        return new RangeSerializer(resolvedContentSerializer);
    }

    /**
     * Rebuilds the PostgREST range literal, delegating the formatting of each bound to the content
     * serializer
     *
     * @param value range to serialize
     * @param gen   json generator
     * @param ctxt  serialization context
     * @throws JacksonException if a bound cannot be serialized
     */
    @Override
    public void serialize(Range<Object> value, JsonGenerator gen, SerializationContext ctxt) throws JacksonException {
        String lower = toRaw(value.getLower(), ctxt);
        String upper = toRaw(value.getUpper(), ctxt);
        String lowerBound = value.isLowerInclusive() ? "[" : "(";
        String upperBound = value.isUpperInclusive() ? "]" : ")";
        gen.writeString(lowerBound + lower + "," + upper + upperBound);
    }

    /**
     * Converts a bound {@code T} (eg. {@code LocalDate}, {@code Integer}) into its raw
     * representation (eg. {@code "2026-05-21"}, {@code "1"}) using the content serializer, or
     * {@code ""} when the bound is {@code null} (unbounded range)
     */
    private String toRaw(Object value, SerializationContext ctxt) throws JacksonException {
        if (value == null) {
            return "";
        }
        ValueSerializer<Object> serializer =
                contentSerializer != null ? contentSerializer : ctxt.findValueSerializer(value.getClass());

        // serializer writes into a JsonGenerator, not into a java String : the value is written into
        // an in memory buffer, then read back as raw text (without the json quotes) to get the
        // representation expected by the PostgREST range format.
        try (TokenBuffer buffer = TokenBuffer.forGeneration()) {
            serializer.serialize(value, buffer, ctxt);
            JsonParser valueParser = buffer.asParser();
            valueParser.nextToken();
            return valueParser.getValueAsString();
        }
    }
}
