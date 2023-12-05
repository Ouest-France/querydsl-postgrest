package fr.ouestfrance.querydsl.postgrest;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.ouestfrance.querydsl.postgrest.app.*;
import fr.ouestfrance.querydsl.postgrest.model.Page;
import fr.ouestfrance.querydsl.postgrest.model.Pageable;
import fr.ouestfrance.querydsl.postgrest.model.Sort;
import fr.ouestfrance.querydsl.postgrest.model.exceptions.PostgrestRequestException;
import fr.ouestfrance.querydsl.postgrest.utils.QueryStringUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.util.MultiValueMapAdapter;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@Slf4j
class PostgrestRepositoryGetTest extends AbstractRepositoryMockTest {


    @Mock
    private PostgrestClient postgrestClient;

    private PostgrestRepository<Post> repository;

    @BeforeEach
    void beforeEach() {
        repository = new PostRepository(postgrestClient, new ObjectMapper());
    }
    @Test
    void shouldSearchAllPosts() {
        when(postgrestClient.search(anyString(), any(), any())).thenReturn(ok(List.of(new Post(), new Post())));
        Page<Post> search = repository.search(null);
        assertNotNull(search);
        assertNotNull(search.iterator());
        assertEquals(2, search.size());
    }

    private ResponseEntity<List<Object>> ok(List<Object> data) {
        MultiValueMap<String, String> headers = new MultiValueMapAdapter<>(Map.of("Content-Range", List.of("0-" + data.size() + "/" + data.size())));
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
        ArgumentCaptor<MultiValueMap<String, Object>> queryArgs = multiMapCaptor();
        ArgumentCaptor<MultiValueMap<String, Object>> headerArgs = multiMapCaptor();
        when(postgrestClient.search(anyString(), queryArgs.capture(), headerArgs.capture())).thenReturn(ok(List.of(new Post(), new Post())));

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
        assertEquals("like.{Test}", queries.getFirst("title"));
        assertEquals("id,title.desc.nullsfirst,author.nullslast", queries.getFirst("order"));
        assertEquals("*,authors(*)", queries.getFirst("select"));
        assertEquals(2, queries.get("status").size());
        // Assert headers captors
        MultiValueMap<String, Object> value = headerArgs.getValue();
        assertEquals("0-9", value.getFirst("Range"));
        assertEquals("items", value.getFirst("Range-Unit"));
    }

    @Test
    void shouldSearchWithoutOrder() {
        PostRequest request = new PostRequest();
        ArgumentCaptor<MultiValueMap<String, Object>> queryArgs = multiMapCaptor();
        ArgumentCaptor<MultiValueMap<String, Object>> headerArgs = multiMapCaptor();
        when(postgrestClient.search(anyString(), queryArgs.capture(), headerArgs.capture())).thenReturn(ok(List.of(new Post(), new Post())));

        Page<Post> search = repository.search(request, Pageable.ofSize(10));
        assertNotNull(search);
        assertEquals(2, search.size());
        // Assert query captors
        MultiValueMap<String, Object> queries = queryArgs.getValue();
        log.info("queries {}", queries);
        assertNull(queries.getFirst("order"));
        // Assert headers captors
        MultiValueMap<String, Object> value = headerArgs.getValue();
        assertEquals("0-9", value.getFirst("Range"));
        assertEquals("items", value.getFirst("Range-Unit"));
    }

    @Test
    void shouldRaiseExceptionOnMultipleOne() {
        when(postgrestClient.search(anyString(), any(), any())).thenReturn(ok(List.of(new Post(), new Post())));
        assertThrows(PostgrestRequestException.class, () -> repository.findOne(null));
    }

    @Test
    void shouldFindOne() {
        when(postgrestClient.search(anyString(), any(), any())).thenReturn(ok(List.of(new Post())));
        Optional<Post> one = repository.findOne(null);
        assertNotNull(one);
        assertTrue(one.isPresent());
    }


    @Test
    void shouldFindEmptyOne() {
        when(postgrestClient.search(anyString(), any(), any())).thenReturn(ok(List.of()));
        Optional<Post> one = repository.findOne(null);
        assertNotNull(one);
        assertTrue(one.isEmpty());
    }


    @Test
    void shouldGetOne() {
        when(postgrestClient.search(anyString(), any(), any())).thenReturn(ok(List.of(new Post())));
        Post one = repository.getOne(null);
        assertNotNull(one);
    }

    @Test
    void shouldRaiseExceptionOnEmptyGetOne() {
        when(postgrestClient.search(anyString(), any(), any())).thenReturn(ok(List.of()));
        assertThrows(PostgrestRequestException.class, () -> repository.getOne(null));
    }

    @Test
    void shouldSearchWithJoin() {
        PostRequestWithSize request = new PostRequestWithSize();
        request.setSize("25");
        ArgumentCaptor<MultiValueMap<String, Object>> queryArgs = multiMapCaptor();
        when(postgrestClient.search(anyString(), queryArgs.capture(), any())).thenReturn(ok(List.of(new Post(), new Post())));
        Page<Post> search = repository.search(request, Pageable.unPaged());
        assertNotNull(search);
        assertEquals(2, search.size());
        // Assert query captors
        MultiValueMap<String, Object> queries = queryArgs.getValue();
        String queryString = QueryStringUtils.toQueryString(queries);
        log.info("queries {}", queries);
        assertEquals(1, queries.get("or").size());
        // Means that you have to make (format.minSize.gte.25 AND format.maxSize.lte.25) OR size.eq.25
        assertEquals("(and(filterFormats.minSize.gte.25,or(filterFormats.maxSize.lte.25,filterFormats.maxSize.is.null)),size.eq.25)", queries.getFirst("or"));
    }

    @Test
    void shouldSearchWithOrOnMultiple() {
        PostRequestWithAuthorOrSubject request = new PostRequestWithAuthorOrSubject();
        request.setAuthor("IA");
        request.setSubject("IA");
        ArgumentCaptor<MultiValueMap<String, Object>> queryArgs = multiMapCaptor();
        when(postgrestClient.search(anyString(), queryArgs.capture(), any())).thenReturn(ok(List.of(new Post(), new Post())));

        Page<Post> search = repository.search(request, Pageable.ofSize(10));
        assertNotNull(search);
        assertEquals(2, search.size());
        // Assert query captors
        MultiValueMap<String, Object> queries = queryArgs.getValue();
        String queryString = QueryStringUtils.toQueryString(queries);
        System.out.println("queries : " + queryString);
        log.info("queries {}", queries);
        assertEquals("(subject.eq.IA,author.name.eq.IA)", queries.getFirst("or"));
        String[] selects = Objects.requireNonNull(queries.getFirst("select")).toString().split(",");
        assertEquals(3, selects.length);
        assertTrue(Arrays.asList(selects).contains("author:authors!inner(name)"));
    }

    @Test
    void testPublicationRequest() {
        PublicationRequest request = new PublicationRequest();
        request.setCode("25");
        request.setPortee("['DEPARTEMENT']");
        request.setDateValide(LocalDate.of(2023,12,4));
        ArgumentCaptor<MultiValueMap<String, Object>> queryArgs = multiMapCaptor();
        when(postgrestClient.search(anyString(), queryArgs.capture(), any())).thenReturn(ok(List.of(new Post(), new Post())));

        Page<Post> search = repository.search(request, Pageable.ofSize(10));
        assertNotNull(search);
        assertEquals(2, search.size());
        // Assert query captors
        MultiValueMap<String, Object> queries = queryArgs.getValue();
        assertEquals("(filtrePublication.dateFinValidite.gte.2023-12-04,filtrePublication.dateFinValidite.is.null)", queries.getFirst("or"));
    }

}
