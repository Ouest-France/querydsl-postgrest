package fr.ouestfrance.querydsl.postgrest;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.ouestfrance.querydsl.postgrest.app.Post;
import fr.ouestfrance.querydsl.postgrest.app.PostDeleteRequest;
import fr.ouestfrance.querydsl.postgrest.app.PostRepository;
import lombok.extern.slf4j.Slf4j;
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
class PostgrestRepositoryDeleteTest extends AbstractRepositoryMockTest{

    @InjectMocks
    private PostgrestRepository<Post> repository = new PostRepository();

    @Mock
    private PostgrestClient postgrestClient;

    @Mock
    private ObjectMapper mapper;


    @Test
    void shouldDelete() {
        ArgumentCaptor<MultiValueMap<String, Object>> queriesCaptor = multiMapCaptor();
        ArgumentCaptor<MultiValueMap<String, Object>> headerCaptor = multiMapCaptor();
        Post deletedPost = new Post();
        when(postgrestClient.delete(anyString(), queriesCaptor.capture(), headerCaptor.capture())).thenReturn(List.of(deletedPost));
        when(mapper.convertValue(any(), eq(Post.class))).thenReturn(deletedPost);
        List<Post> delete = repository.delete(new PostDeleteRequest(List.of("1", "2")));
        assertNotNull(delete);

        MultiValueMap<String, Object> queries = queriesCaptor.getValue();
        assertEquals("in.(1,2)", queries.getFirst("id"));
        MultiValueMap<String, Object> headers = headerCaptor.getValue();
        assertEquals("return=representation", headers.getFirst("Prefer"));
    }
}
