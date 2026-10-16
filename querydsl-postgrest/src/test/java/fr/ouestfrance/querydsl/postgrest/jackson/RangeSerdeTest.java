package fr.ouestfrance.querydsl.postgrest.jackson;

import fr.ouestfrance.querydsl.postgrest.model.Range;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.json.JsonMapper;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RangeSerdeTest {

    // findAndAddModules also asserts that PostgrestRangeModule is discovered through ServiceLoader
    private final JsonMapper jsonMapper = JsonMapper.builder()
            .findAndAddModules()
            .build();

    private JavaType rangeOf(Class<?> boundType) {
        return jsonMapper.getTypeFactory().constructParametricType(Range.class, boundType);
    }

    @Test
    void shouldDeserializeDateRangeWithExclusiveUpperBound() {
        Range<LocalDate> period = jsonMapper.readValue("\"[2026-05-21,2026-09-01)\"", rangeOf(LocalDate.class));

        assertEquals(LocalDate.of(2026, 5, 21), period.getLower());
        assertEquals(LocalDate.of(2026, 9, 1), period.getUpper());
        assertTrue(period.isLowerInclusive());
        assertFalse(period.isUpperInclusive());
    }

    @Test
    void shouldDeserializeUnboundedDateRange() {
        Range<LocalDate> period = jsonMapper.readValue("\"[2026-05-21,)\"", rangeOf(LocalDate.class));

        assertEquals(LocalDate.of(2026, 5, 21), period.getLower());
        assertNull(period.getUpper());
        assertTrue(period.isLowerInclusive());
    }

    @Test
    void shouldDeserializeNumRange() {
        Range<Integer> range = jsonMapper.readValue("\"[1,5)\"", rangeOf(Integer.class));

        assertEquals(1, range.getLower());
        assertEquals(5, range.getUpper());
        assertTrue(range.isLowerInclusive());
        assertFalse(range.isUpperInclusive());
    }

    /**
     * Accepted literals of the package documentation, kept in sync with its table
     */
    @ParameterizedTest
    @CsvSource(nullValues = "null", value = {
            "'[2026-05-21,2026-09-01)', 2026-05-21, 2026-09-01, true,  false",
            "'(2026-05-21,2026-09-01]', 2026-05-21, 2026-09-01, false, true",
            "'[2026-05-21,)',           2026-05-21, null,       true,  false",
            "'(,2026-09-01]',           null,       2026-09-01, false, true",
            "'[,)',                     null,       null,       true,  false",
            "'[ 2026-05-21 , 2026-09-01 )', 2026-05-21, 2026-09-01, true, false",
    })
    void shouldDeserializeDocumentedLiteral(String literal, LocalDate lower, LocalDate upper,
                                            boolean lowerInclusive, boolean upperInclusive) {
        Range<LocalDate> period = jsonMapper.readValue("\"" + literal + "\"", rangeOf(LocalDate.class));

        assertEquals(lower, period.getLower());
        assertEquals(upper, period.getUpper());
        assertEquals(lowerInclusive, period.isLowerInclusive());
        assertEquals(upperInclusive, period.isUpperInclusive());
    }

    /**
     * Rejected literals of the package documentation, kept in sync with its list
     */
    @ParameterizedTest
    @ValueSource(strings = {
            "2026-05-21,2026-09-01", // no brackets
            "[2026-05-21]",          // no comma
            "[2026-05-21,2026-06-01,2026-09-01)", // more than two bounds
    })
    void shouldRejectInvalidRangeFormat(String literal) {
        assertThrows(IllegalArgumentException.class,
                () -> jsonMapper.readValue("\"" + literal + "\"", rangeOf(LocalDate.class)));
    }

    @Test
    void shouldSerializeDateRangeWithExclusiveUpperBound() {
        Range<LocalDate> period = new Range<>(LocalDate.of(2026, 5, 21), LocalDate.of(2026, 9, 1), true, false);

        assertEquals("\"[2026-05-21,2026-09-01)\"", jsonMapper.writeValueAsString(period));
    }

    @Test
    void shouldSerializeUnboundedDateRange() {
        Range<LocalDate> period = new Range<>(LocalDate.of(2026, 5, 21), null, true, false);

        assertEquals("\"[2026-05-21,)\"", jsonMapper.writeValueAsString(period));
    }

    @Test
    void shouldRoundTripNumRange() {
        Range<Integer> range = new Range<>(1, 5, true, false);

        String json = jsonMapper.writeValueAsString(range);
        Range<Integer> read = jsonMapper.readValue(json, rangeOf(Integer.class));

        assertEquals(range.getLower(), read.getLower());
        assertEquals(range.getUpper(), read.getUpper());
        assertEquals(range.isLowerInclusive(), read.isLowerInclusive());
        assertEquals(range.isUpperInclusive(), read.isUpperInclusive());
    }
}
