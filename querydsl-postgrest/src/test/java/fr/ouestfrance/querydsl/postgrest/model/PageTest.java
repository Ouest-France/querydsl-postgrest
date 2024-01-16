package fr.ouestfrance.querydsl.postgrest.model;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;


class PageTest {

    @Test
    void shouldMap() {
        Page<Integer> map = Page.of("1", "2", "3").map(Integer::parseInt);
        assertNotNull(map);
        assertTrue(map.stream().anyMatch(Objects::nonNull));
    }

    @Test
    void withRange() {
        List<Integer> items = List.of(1, 2, 3);
        PageImpl<Integer> page = new PageImpl<>(items, null, items.size(), 1);
        page.withRange(Range.of("0-3/3"));
        assertNotNull(page);
        assertEquals(3, page.getTotalElements());
        assertEquals(1, page.getTotalPages());
    }

    @Test
    void shouldEmptyPage() {
        Page<Integer> page = Page.empty();
        assertNotNull(page);
        assertEquals(0, page.getTotalElements());
        assertEquals(0, page.getTotalPages());
        assertTrue(page.getData().isEmpty());
    }
}
