package fr.ouestfrance.querydsl.postgrest;

import fr.ouestfrance.querydsl.postgrest.app.Post;
import fr.ouestfrance.querydsl.postgrest.app.PostDeleteRequest;
import fr.ouestfrance.querydsl.postgrest.app.PostRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.springframework.util.MultiValueMap;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@Slf4j
class PostgrestRepositoryPatchMockTest extends AbstractRepositoryMockTest{

    @Mock
    private PostgrestClient webClient;

    private PostgrestRepository<Post> repository;

    @BeforeEach
    void beforeEach() {
        repository = new PostRepository(webClient);
    }
    @Test
    void shouldDelete() {
        ArgumentCaptor<MultiValueMap<String, String>> queriesCaptor = multiMapCaptor();
        ArgumentCaptor<MultiValueMap<String, String>> headerCaptor = multiMapCaptor();
        Post post = new Post();
        post.setUserId(26);
        when(webClient.patch(anyString(), queriesCaptor.capture(), eq(post), headerCaptor.capture(), eq(Post.class))).thenReturn(List.of(post));
        List<Post> patched = repository.patch(new PostDeleteRequest(List.of("1", "2")), post);
        assertNotNull(patched);
        assertEquals(1, patched.size());
        MultiValueMap<String, String> queries = queriesCaptor.getValue();
        assertEquals("in.(1,2)", queries.getFirst("id"));
        MultiValueMap<String, String> headers = headerCaptor.getValue();
        assertEquals("return=representation", headers.getFirst("Prefer"));
    }
}
