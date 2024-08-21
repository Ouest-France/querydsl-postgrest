package fr.ouestfrance.querydsl.postgrest.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


class RangeTest {

    @Test
    void shouldCreateRange() {
        HeaderRange range = HeaderRange.of("0-24/156");
        assertNotNull(range);
        assertEquals(0, range.getOffset());
        assertEquals(24, range.getLimit());
        assertEquals(156, range.getTotalElements());

        long count = range.getCount();
        assertEquals(25, count);
    }

    @Test
    void shouldCreateRangeFromUnlimited() {
        HeaderRange range = HeaderRange.of("0-24/*");
        assertNotNull(range);
        assertEquals(0, range.getOffset());
        assertEquals(24, range.getLimit());
        assertEquals(25, range.getTotalElements());

        long count = range.getCount();
        assertEquals(25, count);
    }

}
