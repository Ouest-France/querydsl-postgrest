package fr.ouestfrance.querydsl.postgrest.mappers;

import fr.ouestfrance.querydsl.postgrest.PostgrestFilterOperation;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class PostgrestFilterOperationTest {

    @Test
    void shouldRetrieveOperation() {
        assertDoesNotThrow(PostgrestFilterOperation.ILIKE::new);
        assertDoesNotThrow(PostgrestFilterOperation.CS::new);
        assertDoesNotThrow(PostgrestFilterOperation.CD::new);
    }

}
