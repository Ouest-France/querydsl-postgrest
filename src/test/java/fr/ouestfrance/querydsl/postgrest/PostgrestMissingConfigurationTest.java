package fr.ouestfrance.querydsl.postgrest;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.ouestfrance.querydsl.postgrest.model.exceptions.MissingConfigurationException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

class PostgrestMissingConfigurationTest {

    static class MyRepository extends PostgrestRepository<String> {
        protected MyRepository(PostgrestClient client, ObjectMapper mapper) {
            super(client, mapper);
        }
    }

    @Test
    void shouldRaiseExceptionIfMissingConfiguration() {
        assertThrows(MissingConfigurationException.class, () -> new MyRepository(null, null));
    }

}
