package fr.ouestfrance.querydsl.postgrest;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.ouestfrance.querydsl.postgrest.app.Post;
import fr.ouestfrance.querydsl.postgrest.app.PostRepository;
import fr.ouestfrance.querydsl.postgrest.app.PostRequest;
import fr.ouestfrance.querydsl.postgrest.model.Headers;
import fr.ouestfrance.querydsl.postgrest.model.Page;
import fr.ouestfrance.querydsl.postgrest.model.Pageable;
import fr.ouestfrance.querydsl.postgrest.model.Sort;
import fr.ouestfrance.querydsl.postgrest.model.exceptions.PostgrestRequestException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.util.MultiValueMapAdapter;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@Slf4j
class PostgrestRepositoryGetTest {

    @InjectMocks
    private PostgrestRepository<Post> repository = new PostRepository();

    @Mock
    private PostgrestClient postgrestClient;

    @Mock
    private ObjectMapper mapper;


    @SuppressWarnings("unchecked")
    private ArgumentCaptor<MultiValueMap<String, Object>> queriesCaptor() {
        return ArgumentCaptor.forClass(MultiValueMap.class);
    }
    @SuppressWarnings("unchecked")
    private ArgumentCaptor<Map<String, Object>> headersCaptor() {
        return ArgumentCaptor.forClass(Map.class);
    }

    @Test
    void shouldSearchAllPosts() {
        when(postgrestClient.search(anyString(), any(), anyMap())).thenReturn(ok(List.of(new Post(), new Post())));
        Page<Post> search = repository.search(null);
        assertNotNull(search);
        assertNotNull(search.iterator());
        assertEquals(2, search.size());
    }

    private ResponseEntity<List<Object>> ok(List<Object> data) {
        MultiValueMap<String, String> headers = new MultiValueMapAdapter<>(Map.of(Headers.CONTENT_RANGE, List.of("0-" + data.size() + "/" + data.size())));
        return new ResponseEntity<>(data, headers, HttpStatus.OK);
    }

    @Test
    void shouldSearchWithPaginate() {
        PostRequest request = new PostRequest();
        request.setUserId(1);
        request.setId(1);
        request.setTitle("Test");
        request.setCodes(List.of("a", "b", "c"));
        request.setExcludes(List.of("z"));
        request.setValidDate(LocalDate.of(2023, 11, 10));
        ArgumentCaptor<MultiValueMap<String, Object>> queryArgs = queriesCaptor();
        ArgumentCaptor<Map<String, Object>> headerArgs = headersCaptor();
        when(postgrestClient.search(anyString(), queryArgs.capture(), headerArgs.capture())).thenReturn(ok(List.of(new Post(), new Post())));
        when(mapper.convertValue(any(), eq(Post.class))).thenReturn(new Post());

        Page<Post> search = repository.search(request, Pageable.ofSize(10, Sort.by(Sort.Order.asc("id"), Sort.Order.desc("title").nullsFirst(), Sort.Order.asc("author").nullsLast())));
        assertNotNull(search);
        assertEquals(2, search.size());
        // Assert query captors
        MultiValueMap<String, Object> queries = queryArgs.getValue();
        log.info("queries {}", queries);
        assertEquals("eq.1", queries.getFirst("userId"));
        assertEquals("neq.1", queries.getFirst("id"));
        assertEquals("lte.2023-11-10", queries.getFirst("startDate"));
        assertEquals("(endDate.gte.2023-11-10,endDate.is.null)", queries.getFirst("or"));
        assertEquals("cs.{Test}", queries.getFirst("title"));
        assertEquals("id,title.desc.nullsfirst,author.nullslast", queries.getFirst("order"));
        assertEquals("*,authors(*)", queries.getFirst("select"));
        assertEquals(2, queries.get("status").size());
        // Assert headers captors
        Map<String, Object> value = headerArgs.getValue();
        assertEquals("0-9", value.get(Headers.RANGE));
        assertEquals("items", value.get(Headers.RANGE_UNIT));
    }

    @Test
    void shouldSearchWithoutOrder() {
        PostRequest request = new PostRequest();
        ArgumentCaptor<MultiValueMap<String, Object>> queryArgs = queriesCaptor();
        ArgumentCaptor<Map<String, Object>> headerArgs = headersCaptor();
        when(postgrestClient.search(anyString(), queryArgs.capture(), headerArgs.capture())).thenReturn(ok(List.of(new Post(), new Post())));
        when(mapper.convertValue(any(), eq(Post.class))).thenReturn(new Post());

        Page<Post> search = repository.search(request, Pageable.ofSize(10));
        assertNotNull(search);
        assertEquals(2, search.size());
        // Assert query captors
        MultiValueMap<String, Object> queries = queryArgs.getValue();
        log.info("queries {}", queries);
        assertNull(queries.getFirst("order"));
        // Assert headers captors
        Map<String, Object> value = headerArgs.getValue();
        assertEquals("0-9", value.get(Headers.RANGE));
        assertEquals("items", value.get(Headers.RANGE_UNIT));
    }

    @Test
    void shouldRaiseExceptionOnMultipleOne() {
        when(postgrestClient.search(anyString(), any(), anyMap())).thenReturn(ok(List.of(new Post(), new Post())));
        assertThrows(PostgrestRequestException.class, () -> repository.findOne(null));
    }

    @Test
    void shouldFindOne() {
        when(postgrestClient.search(anyString(), any(), anyMap())).thenReturn(ok(List.of(new Post())));
        when(mapper.convertValue(any(), eq(Post.class))).thenReturn(new Post());
        Optional<Post> one = repository.findOne(null);
        assertNotNull(one);
        assertTrue(one.isPresent());
    }


    @Test
    void shouldFindEmptyOne() {
        when(postgrestClient.search(anyString(), any(), anyMap())).thenReturn(ok(List.of()));
        Optional<Post> one = repository.findOne(null);
        assertNotNull(one);
        assertTrue(one.isEmpty());
    }


    @Test
    void shouldGetOne() {
        when(postgrestClient.search(anyString(), any(), anyMap())).thenReturn(ok(List.of(new Post())));
        when(mapper.convertValue(any(), eq(Post.class))).thenReturn(new Post());
        Post one = repository.getOne(null);
        assertNotNull(one);
    }

    @Test
    void shouldRaiseExceptionOnEmptyGetOne() {
        when(postgrestClient.search(anyString(), any(), anyMap())).thenReturn(ok(List.of()));
        assertThrows(PostgrestRequestException.class, () -> repository.getOne(null));
    }
}
