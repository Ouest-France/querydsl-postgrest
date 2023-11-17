package fr.ouestfrance.querydsl.postgrest.mappers;

import fr.ouestfrance.querydsl.FilterOperation;
import fr.ouestfrance.querydsl.model.FilterFieldInfoModel;
import fr.ouestfrance.querydsl.postgrest.model.exceptions.PostgrestRequestException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

class InMapperTest {

    @Test
    void shouldRaiseExceptionIfNotCollection(){
        InMapper inMapper = new InMapper();
        assertThrows(PostgrestRequestException.class, ()->inMapper.map(new FilterFieldInfoModel("code", FilterOperation.IN, false), "value"));
    }
}
