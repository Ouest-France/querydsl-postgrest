package fr.ouestfrance.querydsl.postgrest.mappers;

import fr.ouestfrance.querydsl.model.SimpleFilter;
import fr.ouestfrance.querydsl.postgrest.PostgrestFilterOperation;
import fr.ouestfrance.querydsl.postgrest.model.Filter;
import fr.ouestfrance.querydsl.postgrest.model.Range;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class NotExtendRightMapperTest {

    private final NotExtendRightMapper mapper = new NotExtendRightMapper();

    @Test
    void shouldMapNotExtendRight() {
        SimpleFilter filter = new SimpleFilter("range", PostgrestFilterOperation.NXR.class, false, null);
        Filter result = mapper.map(filter, Range.between(1, 10));
        assertEquals("nxr.[1,10]", result.getFilterString());
    }
}
