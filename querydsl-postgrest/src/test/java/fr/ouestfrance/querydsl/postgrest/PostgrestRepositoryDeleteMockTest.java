package fr.ouestfrance.querydsl.postgrest;

import fr.ouestfrance.querydsl.postgrest.app.Post;
import fr.ouestfrance.querydsl.postgrest.app.PostDeleteRequest;
import fr.ouestfrance.querydsl.postgrest.app.PostRepository;
import fr.ouestfrance.querydsl.postgrest.model.BulkResponse;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@Slf4j
class PostgrestRepositoryDeleteMockTest extends AbstractRepositoryMockTest {


    @Mock
    private PostgrestClient postgrestClient;

    private PostgrestRepository<Post> repository;

    @BeforeEach
    void beforeEach() {
        repository = new PostRepository(postgrestClient);
    }

    @Test
    void shouldDelete() {
        ArgumentCaptor<Map<String, List<String>>> queriesCaptor = multiMapCaptor();
        ArgumentCaptor<Map<String, List<String>>> headerCaptor = multiMapCaptor();
        Post deletedPost = new Post();
        when(postgrestClient.delete(anyString(), queriesCaptor.capture(), headerCaptor.capture(), eq(Post.class))).thenReturn(BulkResponse.of(deletedPost));
        List<Post> delete = repository.delete(new PostDeleteRequest(List.of("1", "2")));
        assertNotNull(delete);

        Map<String, List<String>> queries = queriesCaptor.getValue();
        assertEquals("in.(1,2)", queries.get("id").stream().findFirst().orElseThrow());
        Map<String, List<String>> headers = headerCaptor.getValue();
        assertEquals("return=representation", headers.get("Prefer").stream().findFirst().orElseThrow());
    }
}
