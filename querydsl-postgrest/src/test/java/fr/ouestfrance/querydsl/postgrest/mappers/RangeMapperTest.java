package fr.ouestfrance.querydsl.postgrest.mappers;

import fr.ouestfrance.querydsl.FilterOperation;
import fr.ouestfrance.querydsl.model.SimpleFilter;
import fr.ouestfrance.querydsl.postgrest.model.Filter;
import fr.ouestfrance.querydsl.postgrest.model.Range;
import fr.ouestfrance.querydsl.postgrest.model.exceptions.PostgrestRequestException;
import fr.ouestfrance.querydsl.postgrest.model.impl.CompositeFilter;
import fr.ouestfrance.querydsl.postgrest.model.impl.QueryFilter;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RangeMapperTest {

    private final RangeMapper mapper = new RangeMapper();

    @Test
    void shouldRaiseExceptionIfNotRange() {
        SimpleFilter filter = new SimpleFilter("code", FilterOperation.BETWEEN.class, false, null);
        assertThrows(PostgrestRequestException.class, () -> mapper.map(filter, "value"));
    }


    @Test
    void shouldReturnCompositeRange() {
        SimpleFilter filter = new SimpleFilter("date", FilterOperation.BETWEEN.class, false, null);
        Filter resultFilter = mapper.map(filter, Range.between("2021-01-01", "2021-01-31"));
        assertInstanceOf(CompositeFilter.class, resultFilter);
        String filterString = resultFilter.getFilterString();
        assertEquals("(date.gte.2021-01-01,date.lte.2021-01-31)", filterString);
    }


    @Test
    void shouldReturnCompositeRangeExclusive() {
        SimpleFilter filter = new SimpleFilter("date", FilterOperation.BETWEEN.class, false, null);
        Filter resultFilter = mapper.map(filter, Range.exclusiveBetween("2021-01-01", "2021-01-31"));
        assertInstanceOf(CompositeFilter.class, resultFilter);
        String filterString = resultFilter.getFilterString();
        assertEquals("(date.gt.2021-01-01,date.lt.2021-01-31)", filterString);
    }

    @Test
    void shouldReturnSimpleFilterOnRangeWithRightEmptyValue() {
        SimpleFilter filter = new SimpleFilter("date", FilterOperation.BETWEEN.class, false, null);
        Filter resultFilter = mapper.map(filter, Range.between("2021-01-01", null));
        assertInstanceOf(QueryFilter.class, resultFilter);
        String filterString = resultFilter.getFilterString();
        assertEquals("gte.2021-01-01", filterString);
    }

    @Test
    void shouldReturnSimpleFilterOnRangeWithLeftEmptyValue() {
        SimpleFilter filter = new SimpleFilter("date", FilterOperation.BETWEEN.class, false, null);
        Filter resultFilter = mapper.map(filter, Range.between(null, "2021-01-01"));
        assertInstanceOf(QueryFilter.class, resultFilter);
        String filterString = resultFilter.getFilterString();
        assertEquals("lte.2021-01-01", filterString);
    }

    @Test
    void shouldReturnSimpleFilterOnRangeWithRightEmptyValueExclusive() {
        SimpleFilter filter = new SimpleFilter("date", FilterOperation.BETWEEN.class, false, null);
        Filter resultFilter = mapper.map(filter, Range.exclusiveBetween("2021-01-01", null));
        assertInstanceOf(QueryFilter.class, resultFilter);
        String filterString = resultFilter.getFilterString();
        assertEquals("gt.2021-01-01", filterString);
    }

    @Test
    void shouldReturnSimpleFilterOnRangeWithLeftEmptyValueExclusive() {
        SimpleFilter filter = new SimpleFilter("date", FilterOperation.BETWEEN.class, false, null);
        Filter resultFilter = mapper.map(filter, Range.exclusiveBetween(null, "2021-01-01"));
        assertInstanceOf(QueryFilter.class, resultFilter);
        String filterString = resultFilter.getFilterString();
        assertEquals("lt.2021-01-01", filterString);
    }
}
