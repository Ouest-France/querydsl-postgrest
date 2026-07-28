package fr.ouestfrance.querydsl.postgrest.mappers;

import fr.ouestfrance.querydsl.model.SimpleFilter;
import fr.ouestfrance.querydsl.postgrest.PostgrestFilterOperation;
import fr.ouestfrance.querydsl.postgrest.model.Filter;
import fr.ouestfrance.querydsl.postgrest.model.Range;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AdjacentMapperTest {

    private final AdjacentMapper mapper = new AdjacentMapper();

    @Test
    void shouldMapAdjacent() {
        SimpleFilter filter = new SimpleFilter("range", PostgrestFilterOperation.ADJ.class, false, null);
        Filter result = mapper.map(filter, Range.exclusiveBetween(1, 10));
        assertEquals("adj.(1,10)", result.getFilterString());
    }
}
