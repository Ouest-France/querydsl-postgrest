package fr.ouestfrance.querydsl.postgrest.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SortTest {

    @Test
    void shouldTestSortObject(){
        Sort sortA = Sort.by("sortA");
        assertEquals(1, sortA.getOrders().size());
        assertEquals(Sort.Direction.ASC, sortA.getOrders().get(0).getDirection());

        Sort descending = sortA.descending();
        assertEquals(1, descending.getOrders().size());
        assertEquals(Sort.Direction.DESC, descending.getOrders().get(0).getDirection());

        Sort ascending = descending.ascending();
        assertEquals(1, ascending.getOrders().size());
        assertEquals(Sort.Direction.ASC, ascending.getOrders().get(0).getDirection());
    }
}
