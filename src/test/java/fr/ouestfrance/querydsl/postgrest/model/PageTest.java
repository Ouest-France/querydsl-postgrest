package fr.ouestfrance.querydsl.postgrest.model;

import org.junit.jupiter.api.Test;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;


class PageTest {

    @Test
    void shouldMap() {
        Page<Integer> map = Page.of("1", "2", "3").map(Integer::parseInt);
        assertNotNull(map);
        assertTrue(map.stream().anyMatch(Objects::nonNull));
    }

}
