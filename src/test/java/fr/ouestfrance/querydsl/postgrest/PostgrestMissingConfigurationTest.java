package fr.ouestfrance.querydsl.postgrest;

import fr.ouestfrance.querydsl.postgrest.model.exceptions.MissingConfigurationException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

class PostgrestMissingConfigurationTest {

    static class MyRepository extends PostgrestRepository<String>{

    }

    @Test
    void shouldRaiseExceptionIfMissingConfiguration(){
        assertThrows(MissingConfigurationException.class, MyRepository::new);
    }

}
