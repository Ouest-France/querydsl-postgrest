package fr.ouestfrance.querydsl.postgrest.mappers;

import static org.junit.jupiter.api.Assertions.assertThrows;

import fr.ouestfrance.querydsl.FilterOperation;
import fr.ouestfrance.querydsl.model.SimpleFilter;
import fr.ouestfrance.querydsl.postgrest.model.exceptions.PostgrestRequestException;
import org.junit.jupiter.api.Test;

class NotInMapperTest {

    @Test
    void shouldRaiseExceptionIfNotCollection() {
        NotInMapper notInMapper = new NotInMapper();
        SimpleFilter filter = new SimpleFilter("code", FilterOperation.IN.class, false, null);
        assertThrows(PostgrestRequestException.class, () -> notInMapper.map(filter, "value"));
    }
}
