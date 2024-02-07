package fr.ouestfrance.querydsl.postgrest;

import fr.ouestfrance.querydsl.postgrest.app.Post;
import fr.ouestfrance.querydsl.postgrest.app.PostDeleteRequest;
import fr.ouestfrance.querydsl.postgrest.app.PostRepository;
import fr.ouestfrance.querydsl.postgrest.model.BulkOptions;
import fr.ouestfrance.querydsl.postgrest.model.BulkResponse;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Slf4j
class PostgrestRepositoryPatchMockTest extends AbstractRepositoryMockTest {

    @Mock
    private PostgrestClient webclient;

    private PostgrestRepository<Post> repository;

    @BeforeEach
    void beforeEach() {
        repository = new PostRepository(webclient);
    }

    @Test
    void shouldPatch() {
        ArgumentCaptor<Map<String, List<String>>> queriesCaptor = multiMapCaptor();
        ArgumentCaptor<Map<String, List<String>>> headerCaptor = multiMapCaptor();
        Post post = new Post();
        post.setUserId(26);
        when(webclient.patch(anyString(), queriesCaptor.capture(), eq(post), headerCaptor.capture(), eq(Post.class))).thenReturn(BulkResponse.of(post));
        List<Post> patched = repository.patch(new PostDeleteRequest(List.of("1", "2")), post);
        assertNotNull(patched);
        assertEquals(1, patched.size());
        Map<String, List<String>> queries = queriesCaptor.getValue();
        assertEquals("in.(1,2)", queries.get("id").stream().findFirst().orElseThrow());
        Map<String, List<String>> headers = headerCaptor.getValue();
        assertTrue(headers.get("Prefer").contains("return=representation"));
    }


    @Test
    void shouldBulkPatchInBulkMode() {
        ArgumentCaptor<Map<String, List<String>>> queriesCaptor = multiMapCaptor();
        ArgumentCaptor<Map<String, List<String>>> headerCaptor = multiMapCaptor();
        Post post = new Post();
        post.setUserId(26);
        when(webclient.patch(anyString(), queriesCaptor.capture(), eq(post), headerCaptor.capture(), eq(Post.class))).thenReturn(new BulkResponse<>(null, 50, 450));

        // Should patch all data by chunk of 100
        BulkResponse<Post> patched = repository.patch(new PostDeleteRequest(List.of("1", "2")), post, BulkOptions.builder()
                        .countOnly(true)
                        .pageSize(50)
                .build());
        assertNotNull(patched);
        assertEquals(0, patched.size());
        assertEquals(9, headerCaptor.getAllValues().size());
        assertEquals(450, patched.getTotalElements());
        assertEquals(450, patched.getAffectedRows());
        // Check that Bulk make 9 calls
        headerCaptor.getAllValues().stream().map(x->x.get("Range").stream().findFirst().orElse(null)).filter(Objects::nonNull).forEach(System.out::println);
    }
}
