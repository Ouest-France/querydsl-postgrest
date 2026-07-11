package fr.ouestfrance.querydsl.postgrest.mappers;

import fr.ouestfrance.querydsl.model.SimpleFilter;
import fr.ouestfrance.querydsl.postgrest.PostgrestFilterOperation;
import fr.ouestfrance.querydsl.postgrest.model.Filter;
import fr.ouestfrance.querydsl.postgrest.model.Range;
import fr.ouestfrance.querydsl.postgrest.model.exceptions.PostgrestRequestException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OverlapMapperTest {

    private final OverlapMapper mapper = new OverlapMapper();

    @Test
    void shouldMapOverlap() {
        SimpleFilter filter = new SimpleFilter("period", PostgrestFilterOperation.OV.class, false, null);
        Filter result = mapper.map(filter, Range.between("2017-01-01", "2017-06-30"));
        assertEquals("ov.[2017-01-01,2017-06-30]", result.getFilterString());
    }

    @Test
    void shouldMapOverlapExclusive() {
        SimpleFilter filter = new SimpleFilter("period", PostgrestFilterOperation.OV.class, false, null);
        Filter result = mapper.map(filter, Range.exclusiveBetween("2017-01-01", "2017-06-30"));
        assertEquals("ov.(2017-01-01,2017-06-30)", result.getFilterString());
    }

    @Test
    void shouldRaiseExceptionIfNotRange() {
        SimpleFilter filter = new SimpleFilter("period", PostgrestFilterOperation.OV.class, false, null);
        assertThrows(PostgrestRequestException.class, () -> mapper.map(filter, "value"));
    }
}