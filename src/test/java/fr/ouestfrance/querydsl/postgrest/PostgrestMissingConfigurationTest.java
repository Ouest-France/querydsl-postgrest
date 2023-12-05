package fr.ouestfrance.querydsl.postgrest;

import fr.ouestfrance.querydsl.postgrest.model.exceptions.MissingConfigurationException;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;

import static org.junit.jupiter.api.Assertions.assertThrows;

class PostgrestMissingConfigurationTest {

    static class MyRepository extends PostgrestRepository<String> {
        public MyRepository(PostgrestClient client) {
            super(client);
        }
    }

    @Test
    void shouldRaiseExceptionIfMissingConfiguration() {
        assertThrows(MissingConfigurationException.class, () -> new MyRepository(null));
    }

}
