package fr.ouestfrance.querydsl.postgrest.mappers;

import fr.ouestfrance.querydsl.FilterOperation;
import fr.ouestfrance.querydsl.model.FilterFieldInfoModel;
import fr.ouestfrance.querydsl.postgrest.model.exceptions.PostgrestRequestException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

class NotInMapperTest {

    @Test
    void shouldRaiseExceptionIfNotCollection(){
        NotInMapper notInMapper = new NotInMapper();
        assertThrows(PostgrestRequestException.class, ()->notInMapper.map(new FilterFieldInfoModel("code", FilterOperation.IN, false), "value"));
    }
}
