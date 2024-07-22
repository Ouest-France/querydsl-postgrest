package fr.ouestfrance.querydsl.postgrest;

import fr.ouestfrance.querydsl.postgrest.app.Post;
import fr.ouestfrance.querydsl.postgrest.app.PostRepository;
import fr.ouestfrance.querydsl.postgrest.model.BulkOptions;
import fr.ouestfrance.querydsl.postgrest.model.BulkResponse;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@Slf4j
class PostgrestRepositoryPostMockTest extends AbstractRepositoryMockTest {

    @Mock
    private PostgrestClient client;

    private PostgrestRepository<Post> repository;

    @BeforeEach
    void beforeEach() {
        repository = new PostRepository(client);
    }

    @Test
    void shouldPost() {
        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<Object>> postCaptor = ArgumentCaptor.forClass(List.class);
        ArgumentCaptor<Map<String, List<String>>> headerCaptor = multiMapCaptor();
        String generateId = UUID.randomUUID().toString();

        Post save = new Post();
        save.setTitle("title");
        save.setBody("test");

        when(client.post(anyString(), eq(new HashMap<>()), postCaptor.capture(), headerCaptor.capture(), eq(Post.class))).thenAnswer(x -> {
            Post post = new Post();
            post.setId(generateId);
            post.setTitle(save.getTitle());
            post.setBody(save.getBody());
            return BulkResponse.of(post);
        });
        Post saved = repository.post(save);
        assertNotNull(saved);

        assertEquals(1, postCaptor.getValue().size());
        Assertions.assertEquals(generateId, saved.getId());
        Assertions.assertEquals(save.getBody(), saved.getBody());
        Assertions.assertEquals(save.getTitle(), saved.getTitle());
        Map<String, List<String>> headers = headerCaptor.getValue();
        assertEquals(1, headers.get("Prefer").size());
        assertEquals("return=representation", headers.get("Prefer").stream().findFirst().orElseThrow());
    }

    @Test
    void shouldPostInBulkMode() {
        ArgumentCaptor<Map<String, List<String>>> headerCaptor = multiMapCaptor();
        Post save = new Post();
        save.setTitle("title");
        save.setBody("test");

        when(client.post(anyString(),  eq(new HashMap<>()), any(), headerCaptor.capture(), eq(Post.class))).thenReturn(new BulkResponse<>(null, 50, 450));

        // Should patch all data by chunk of 100
        BulkResponse<Post> post = repository.post(List.of(save), BulkOptions.builder()
                .countOnly(true)
                .pageSize(50)
                .build());
        assertNotNull(post);
        assertEquals(0, post.size());
        assertEquals(450, post.getAffectedRows());
        assertEquals(450, post.getTotalElements());
        // Check that Bulk make 9 calls
        assertEquals(9, headerCaptor.getAllValues().size());
        headerCaptor.getAllValues().stream().map(x->x.get("Range").stream().findFirst().orElse(null)).filter(Objects::nonNull).forEach(System.out::println);
    }
}
