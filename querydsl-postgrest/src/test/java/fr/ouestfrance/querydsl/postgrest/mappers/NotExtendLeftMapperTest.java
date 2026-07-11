package fr.ouestfrance.querydsl.postgrest.mappers;

import fr.ouestfrance.querydsl.model.SimpleFilter;
import fr.ouestfrance.querydsl.postgrest.PostgrestFilterOperation;
import fr.ouestfrance.querydsl.postgrest.model.Filter;
import fr.ouestfrance.querydsl.postgrest.model.Range;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class NotExtendLeftMapperTest {

    private final NotExtendLeftMapper mapper = new NotExtendLeftMapper();

    @Test
    void shouldMapNotExtendLeft() {
        SimpleFilter filter = new SimpleFilter("range", PostgrestFilterOperation.NXL.class, false, null);
        Filter result = mapper.map(filter, Range.between(1, 10));
        assertEquals("nxl.[1,10]", result.getFilterString());
    }
}
