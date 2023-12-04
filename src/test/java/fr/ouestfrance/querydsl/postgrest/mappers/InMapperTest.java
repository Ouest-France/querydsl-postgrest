package fr.ouestfrance.querydsl.postgrest.mappers;

import fr.ouestfrance.querydsl.FilterOperation;
import fr.ouestfrance.querydsl.model.SimpleFilter;
import fr.ouestfrance.querydsl.postgrest.model.exceptions.PostgrestRequestException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

class InMapperTest {

    @Test
    void shouldRaiseExceptionIfNotCollection() {
        InMapper inMapper = new InMapper();
        SimpleFilter filter = new SimpleFilter("filter", FilterOperation.IN, false, null);
        assertThrows(PostgrestRequestException.class, () -> inMapper.map(filter, "value"));
    }
}
