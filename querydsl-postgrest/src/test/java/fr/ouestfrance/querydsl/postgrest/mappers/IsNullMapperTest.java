package fr.ouestfrance.querydsl.postgrest.mappers;

import fr.ouestfrance.querydsl.FilterOperation;
import fr.ouestfrance.querydsl.model.SimpleFilter;
import fr.ouestfrance.querydsl.postgrest.builders.QueryFilterVisitor;
import fr.ouestfrance.querydsl.postgrest.model.Filter;
import org.junit.jupiter.api.Test;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class IsNullMapperTest {

    @Test
    void shouldMapIsNull() {
        IsNullMapper mapper = new IsNullMapper();
        assertNotNull(mapper.operation());
        Stream.of(Boolean.TRUE, Boolean.FALSE).forEach(value -> {
            Filter result = mapper.map(new SimpleFilter("name", FilterOperation.ISNULL.class, false, null), value);
            assertNotNull(result);
            QueryFilterVisitor visitor = new QueryFilterVisitor();
            result.accept(visitor);
            assertNotNull(visitor.getValue());
            assertEquals(Boolean.TRUE.equals(value) ? "is.null" : "not.is.null", visitor.getValue());
        });

    }
}
