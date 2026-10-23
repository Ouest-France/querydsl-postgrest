package fr.ouestfrance.querydsl.postgrest;

import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class JsonResponseReaderTest {

    private final JsonResponseReader reader = new JsonResponseReader();

    @Test
    void shouldReadEmptyListOnNullOrEmptyBody() {
        assertEquals(List.of(), reader.readList(null, String.class));
        assertEquals(List.of(), reader.readList(new byte[0], String.class));
    }

    @Test
    void shouldReadEmptyListOnJsonNullBody() {
        assertEquals(List.of(), reader.readList("null".getBytes(StandardCharsets.UTF_8), String.class));
    }

    @Test
    void shouldReadList() {
        assertEquals(List.of("a", "b"), reader.readList("[\"a\",\"b\"]".getBytes(StandardCharsets.UTF_8), String.class));
    }
}
