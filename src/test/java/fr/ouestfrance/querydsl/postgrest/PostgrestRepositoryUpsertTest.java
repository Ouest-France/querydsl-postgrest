package fr.ouestfrance.querydsl.postgrest;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.ouestfrance.querydsl.postgrest.app.Post;
import fr.ouestfrance.querydsl.postgrest.app.PostRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Slf4j
class PostgrestRepositoryUpsertTest extends AbstractRepositoryMockTest {

    @Mock
    private PostgrestClient webClient;

    private PostgrestRepository<Post> repository;

    @BeforeEach
    void beforeEach() {
        repository = new PostRepository(webClient);
    }
    @Test
    void shouldUpsert() {
        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<Object>> postCaptor = ArgumentCaptor.forClass(List.class);
        ArgumentCaptor<MultiValueMap<String, String>> headerCaptor = multiMapCaptor();
        String generateId = UUID.randomUUID().toString();

        Post save = new Post();
        save.setTitle("title");
        save.setBody("test");

        when(webClient.post(anyString(), postCaptor.capture(), headerCaptor.capture())).thenAnswer(x -> {
            Post post = new Post();
            post.setId(generateId);
            post.setTitle(save.getTitle());
            post.setBody(save.getBody());
            return List.of(post);
        });
        Post saved = repository.upsert(save);
        assertNotNull(saved);

        assertEquals(1, postCaptor.getValue().size());
        assertEquals(generateId, saved.getId());
        assertEquals(save.getBody(), saved.getBody());
        assertEquals(save.getTitle(), saved.getTitle());
        MultiValueMap<String, String> headers = headerCaptor.getValue();
        assertEquals(3, headers.get("Prefer").size());
        assertEquals("return=representation", headers.getFirst("Prefer"));
    }
}
