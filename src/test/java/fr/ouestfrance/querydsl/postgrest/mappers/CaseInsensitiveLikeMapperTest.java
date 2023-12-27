package fr.ouestfrance.querydsl.postgrest.mappers;

import fr.ouestfrance.querydsl.model.SimpleFilter;
import fr.ouestfrance.querydsl.postgrest.PostgrestFilterOperation;
import fr.ouestfrance.querydsl.postgrest.builders.QueryFilterVisitor;
import fr.ouestfrance.querydsl.postgrest.model.Filter;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class CaseInsensitiveLikeMapperTest {

    @Test
    void shouldMapCaseInsensitive(){
        CaseInsensitiveLikeMapper mapper = new CaseInsensitiveLikeMapper();
        assertNotNull(mapper.operation());
        Filter result = mapper.map(new SimpleFilter("name", PostgrestFilterOperation.ILIKE.class, false, null), "John");
        assertNotNull(result);
        QueryFilterVisitor visitor = new QueryFilterVisitor();
        result.accept(visitor);
        assertNotNull(visitor.getValue());
    }
}
