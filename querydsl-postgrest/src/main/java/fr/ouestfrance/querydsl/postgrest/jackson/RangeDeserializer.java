package fr.ouestfrance.querydsl.postgrest.jackson;

import fr.ouestfrance.querydsl.postgrest.model.Range;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.BeanProperty;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.util.TokenBuffer;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Deserializes a PostgREST range literal into a {@link Range}, the bound conversion being delegated
 * to the deserializer registered for the bound type. See the
 * {@link fr.ouestfrance.querydsl.postgrest.jackson package documentation} for the accepted format.
 *
 * @see RangeSerializer
 * @see PostgrestRangeModule
 */
public class RangeDeserializer extends ValueDeserializer<Range<Object>> {

    // Groups, in order : opening bracket, lower bound, upper bound, closing bracket.
    private static final Pattern RANGE_PATTERN =
            Pattern.compile("^([\\[(])\\s*([^,]*?)\\s*,\\s*([^,]*?)\\s*([])])$");

    private final ValueDeserializer<Object> contentDeserializer;

    /**
     * Creates a non contextual deserializer, specialized later by {@link #createContextual}
     */
    public RangeDeserializer() {
        this(null);
    }

    private RangeDeserializer(ValueDeserializer<Object> contentDeserializer) {
        this.contentDeserializer = contentDeserializer;
    }

    /**
     * Resolves {@code T} from the generic type of the field and returns a specialized instance
     * holding the delegate deserializer for {@code T}
     *
     * @param ctxt     deserialization context
     * @param property property being deserialized, may be null
     * @return specialized deserializer
     */
    @Override
    public ValueDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property) {
        JavaType wrapperType = property != null ? property.getType() : ctxt.getContextualType();
        JavaType resolvedContentType = wrapperType != null && wrapperType.containedTypeCount() > 0
                ? wrapperType.containedType(0)
                : ctxt.constructType(String.class);
        ValueDeserializer<Object> resolvedContentDeserializer =
                ctxt.findContextualValueDeserializer(resolvedContentType, property);
        return new RangeDeserializer(resolvedContentDeserializer);
    }

    /**
     * Parses the PostgREST range literal and delegates the conversion of each bound to the content
     * deserializer
     *
     * @param p    json parser
     * @param ctxt deserialization context
     * @return parsed range
     * @throws JacksonException if a bound cannot be deserialized
     */
    @Override
    public Range<Object> deserialize(JsonParser p, DeserializationContext ctxt) throws JacksonException {
        String raw = p.getString();
        Matcher matcher = RANGE_PATTERN.matcher(raw.trim());
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Invalid PostgREST range format : " + raw);
        }

        boolean lowerInclusive = "[".equals(matcher.group(1));
        boolean upperInclusive = "]".equals(matcher.group(4));
        Object lower = toValue(matcher.group(2), ctxt);
        Object upper = toValue(matcher.group(3), ctxt);

        return new Range<>(lower, upper, lowerInclusive, upperInclusive);
    }

    /**
     * Converts a raw bound (eg. {@code "2026-05-21"}, {@code "1"}) into {@code T} using the content
     * deserializer, or {@code null} when the bound is missing (unbounded range)
     */
    private Object toValue(String rawValue, DeserializationContext ctxt) throws JacksonException {
        if (rawValue == null || rawValue.isBlank()) {
            return null;
        }
        // contentDeserializer expects a JsonParser positioned on a json value, not a java String :
        // rawValue is replayed as a json string token into an in memory buffer, then read back with
        // a parser so it can be delegated the usual way.
        try (TokenBuffer buffer = TokenBuffer.forGeneration()) {
            buffer.writeString(rawValue);
            JsonParser valueParser = buffer.asParser();
            valueParser.nextToken();
            return contentDeserializer.deserialize(valueParser, ctxt);
        }
    }
}
