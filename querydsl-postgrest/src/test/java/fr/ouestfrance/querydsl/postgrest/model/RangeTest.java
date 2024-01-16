package fr.ouestfrance.querydsl.postgrest.model;

import fr.ouestfrance.querydsl.postgrest.model.Range;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


class RangeTest {

    @Test
    void shouldCreateRange() {
        Range range = Range.of("0-24/156");
        assertNotNull(range);
        assertEquals(0, range.getOffset());
        assertEquals(24, range.getLimit());
        assertEquals(156, range.getTotalElements());
    }

}
