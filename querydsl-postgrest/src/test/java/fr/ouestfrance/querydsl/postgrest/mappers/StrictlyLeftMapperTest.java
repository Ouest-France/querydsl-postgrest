package fr.ouestfrance.querydsl.postgrest.mappers;

import fr.ouestfrance.querydsl.model.SimpleFilter;
import fr.ouestfrance.querydsl.postgrest.PostgrestFilterOperation;
import fr.ouestfrance.querydsl.postgrest.model.Filter;
import fr.ouestfrance.querydsl.postgrest.model.Range;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StrictlyLeftMapperTest {

    private final StrictlyLeftMapper mapper = new StrictlyLeftMapper();

    @Test
    void shouldMapStrictlyLeft() {
        SimpleFilter filter = new SimpleFilter("range", PostgrestFilterOperation.SL.class, false, null);
        Filter result = mapper.map(filter, Range.exclusiveBetween(1, 10));
        assertEquals("sl.(1,10)", result.getFilterString());
    }
}
