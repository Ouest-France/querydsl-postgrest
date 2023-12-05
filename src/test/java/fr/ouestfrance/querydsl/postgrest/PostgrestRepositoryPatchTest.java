package fr.ouestfrance.querydsl.postgrest;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.ouestfrance.querydsl.postgrest.app.Post;
import fr.ouestfrance.querydsl.postgrest.app.PostDeleteRequest;
import fr.ouestfrance.querydsl.postgrest.app.PostRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.util.MultiValueMap;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@Slf4j
class PostgrestRepositoryPatchTest extends AbstractRepositoryMockTest{

    @Mock
    private PostgrestClient postgrestClient;

    private PostgrestRepository<Post> repository;

    @BeforeEach
    void beforeEach() {
        repository = new PostRepository(postgrestClient, new ObjectMapper());
    }
    @Test
    void shouldDelete() {
        ArgumentCaptor<MultiValueMap<String, Object>> queriesCaptor = multiMapCaptor();
        ArgumentCaptor<MultiValueMap<String, Object>> headerCaptor = multiMapCaptor();
        Post post = new Post();
        post.setUserId(26);
        when(postgrestClient.patch(anyString(), queriesCaptor.capture(), eq(post), headerCaptor.capture())).thenReturn(List.of(post));
        List<Post> patched = repository.patch(new PostDeleteRequest(List.of("1", "2")), post);
        assertNotNull(patched);
        assertEquals(1, patched.size());
        MultiValueMap<String, Object> queries = queriesCaptor.getValue();
        assertEquals("in.(1,2)", queries.getFirst("id"));
        MultiValueMap<String, Object> headers = headerCaptor.getValue();
        assertEquals("return=representation", headers.getFirst("Prefer"));
    }
}
