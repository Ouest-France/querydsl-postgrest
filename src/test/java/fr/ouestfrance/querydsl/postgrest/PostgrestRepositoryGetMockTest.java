package fr.ouestfrance.querydsl.postgrest;

import fr.ouestfrance.querydsl.postgrest.app.*;
import fr.ouestfrance.querydsl.postgrest.model.Page;
import fr.ouestfrance.querydsl.postgrest.model.PageImpl;
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
class PostgrestRepositoryGetMockTest extends AbstractRepositoryMockTest {


    @Mock
    private PostgrestWebClient webClient;

    private PostgrestRepository<Post> repository;

    @BeforeEach
    void beforeEach() {
        repository = new PostRepository(webClient);
    }
    @Test
    void shouldSearchAllPosts() {
        when(webClient.search(anyString(), any(), any(), eq(Post.class))).thenReturn(Page.of(new Post(), new Post()));
        Page<Post> search = repository.search(null);
        assertNotNull(search);
        assertNotNull(search.iterator());
        assertEquals(2, search.size());
        assertFalse(search.hasNext());
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
        request.setTitle("Test*");
        request.setCodes(List.of("a", "b", "c"));
        request.setExcludes(List.of("z"));
        request.setValidDate(LocalDate.of(2023, 11, 10));
        ArgumentCaptor<MultiValueMap<String, String>> queryArgs = multiMapCaptor();
        ArgumentCaptor<MultiValueMap<String, String>> headerArgs = multiMapCaptor();
        when(webClient.search(anyString(), queryArgs.capture(), headerArgs.capture(), eq(Post.class))).thenReturn(Page.of(new Post(), new Post()));

        Page<Post> search = repository.search(request, Pageable.ofSize(10, Sort.by(Sort.Order.asc("id"), Sort.Order.desc("title").nullsFirst(), Sort.Order.asc("author").nullsLast())));
        assertNotNull(search);
        assertEquals(2, search.size());
        // Assert query captors
        MultiValueMap<String, String> queries = queryArgs.getValue();
        log.info("queries {}", queries);
        assertEquals("eq.1", queries.getFirst("userId"));
        assertEquals("neq.1", queries.getFirst("id"));
        assertEquals("lte.2023-11-10", queries.getFirst("startDate"));
        assertEquals("(endDate.gte.2023-11-10,endDate.is.null)", queries.getFirst("or"));
        assertEquals("like.Test*", queries.getFirst("title"));
        assertEquals("id,title.desc.nullsfirst,author.nullslast", queries.getFirst("order"));
        assertEquals("*,authors(*)", queries.getFirst("select"));
        assertEquals(2, queries.get("status").size());
        // Assert headers captors
        MultiValueMap<String, String> value = headerArgs.getValue();
        assertEquals("0-9", value.getFirst("Range"));
        assertEquals("items", value.getFirst("Range-Unit"));
        // Assert pagination
        assertNotNull(search.getPageable());
        assertEquals(10, search.getPageable().getPageSize());
        assertEquals(0, search.getPageable().getPageNumber());
        assertEquals(2, search.getTotalElements());
        assertEquals(1, search.getTotalPages());
        assertFalse(search.hasNext());
        assertNotNull(search.getPageable().previous());
        assertEquals(0, search.getPageable().previous().getPageNumber());
    }

    @Test
    void shouldSearchWithNextPaginate() {
        PostRequest request = new PostRequest();
        request.setUserId(1);
        request.setId(1);
        request.setTitle("Test*");
        request.setCodes(List.of("a", "b", "c"));
        request.setExcludes(List.of("z"));
        request.setValidDate(LocalDate.of(2023, 11, 10));
        ArgumentCaptor<MultiValueMap<String, String>> queryArgs = multiMapCaptor();
        ArgumentCaptor<MultiValueMap<String, String>> headerArgs = multiMapCaptor();
        when(webClient.search(anyString(), queryArgs.capture(), headerArgs.capture(), eq(Post.class))).thenReturn(new PageImpl<>(List.of(new Post()), null, 2, 2));

        Page<Post> search = repository.search(request, Pageable.ofSize(1, Sort.by(Sort.Order.asc("id"))));
        assertNotNull(search);
        assertEquals(1, search.size());
        assertTrue(search.hasNext());
        // Assert pagination
        assertNotNull(search.getPageable());
        assertEquals(1, search.getPageable().getPageSize());
        assertEquals(0, search.getPageable().getPageNumber());
        assertEquals(2, search.getTotalElements());
        assertEquals(2, search.getTotalPages());
        // Assert next pagination
        assertNotNull(search.getPageable().next());
        assertEquals(1, search.getPageable().next().getPageSize());
        assertEquals(1, search.getPageable().next().getPageNumber());
        assertEquals(search.getPageable().getSort(), search.getPageable().next().getSort());

    }

    @Test
    void shouldSearchWithPreviousPaginate() {
        PostRequest request = new PostRequest();
        request.setUserId(1);
        request.setId(1);
        request.setTitle("Test*");
        request.setCodes(List.of("a", "b", "c"));
        request.setExcludes(List.of("z"));
        request.setValidDate(LocalDate.of(2023, 11, 10));
        ArgumentCaptor<MultiValueMap<String, String>> queryArgs = multiMapCaptor();
        ArgumentCaptor<MultiValueMap<String, String>> headerArgs = multiMapCaptor();
        when(webClient.search(anyString(), queryArgs.capture(), headerArgs.capture(), eq(Post.class))).thenReturn(new PageImpl<>(List.of(new Post()), null, 2, 2));

        Page<Post> search = repository.search(request, Pageable.ofSize(1, 1, Sort.by(Sort.Order.asc("id"))));
        assertNotNull(search);
        assertEquals(1, search.size());
        assertFalse(search.hasNext());
        // Assert pagination
        assertNotNull(search.getPageable());
        assertEquals(1, search.getPageable().getPageSize());
        assertEquals(1, search.getPageable().getPageNumber());
        assertEquals(2, search.getTotalElements());
        assertEquals(2, search.getTotalPages());
        // Assert previous pagination
        assertNotNull(search.getPageable().previous());
        assertEquals(1, search.getPageable().previous().getPageSize());
        assertEquals(0, search.getPageable().previous().getPageNumber());
    }

    @Test
    void shouldSearchWithoutOrder() {
        PostRequest request = new PostRequest();
        ArgumentCaptor<MultiValueMap<String, String>> queryArgs = multiMapCaptor();
        ArgumentCaptor<MultiValueMap<String, String>> headerArgs = multiMapCaptor();
        when(webClient.search(anyString(), queryArgs.capture(), headerArgs.capture(), eq(Post.class))).thenReturn(Page.of(new Post(), new Post()));

        Page<Post> search = repository.search(request, Pageable.ofSize(10));
        assertNotNull(search);
        assertEquals(2, search.size());
        // Assert query captors
        MultiValueMap<String, String> queries = queryArgs.getValue();
        log.info("queries {}", queries);
        assertNull(queries.getFirst("order"));
        // Assert headers captors
        MultiValueMap<String, String> value = headerArgs.getValue();
        assertEquals("0-9", value.getFirst("Range"));
        assertEquals("items", value.getFirst("Range-Unit"));
    }

    @Test
    void shouldRaiseExceptionOnMultipleOne() {
        when(webClient.search(anyString(), any(), any(), eq(Post.class))).thenReturn(Page.of(new Post(), new Post()));
        assertThrows(PostgrestRequestException.class, () -> repository.findOne(null));
    }

    @Test
    void shouldFindOne() {
        when(webClient.search(anyString(), any(), any(), eq(Post.class))).thenReturn(Page.of(new Post()));
        Optional<Post> one = repository.findOne(null);
        assertNotNull(one);
        assertTrue(one.isPresent());
    }


    @Test
    void shouldFindEmptyOne() {
        when(webClient.search(anyString(), any(), any(), eq(Post.class))).thenReturn(Page.of());
        Optional<Post> one = repository.findOne(null);
        assertNotNull(one);
        assertTrue(one.isEmpty());
    }


    @Test
    void shouldGetOne() {
        when(webClient.search(anyString(), any(), any(), eq(Post.class))).thenReturn(Page.of(new Post()));
        Post one = repository.getOne(null);
        assertNotNull(one);
    }

    @Test
    void shouldRaiseExceptionOnEmptyGetOne() {
        when(webClient.search(anyString(), any(), any(), eq(Post.class))).thenReturn(Page.of());
        assertThrows(PostgrestRequestException.class, () -> repository.getOne(null));
    }

    @Test
    void shouldSearchWithJoin() {
        PostRequestWithSize request = new PostRequestWithSize();
        request.setSize("25");
        ArgumentCaptor<MultiValueMap<String, String>> queryArgs = multiMapCaptor();
        when(webClient.search(anyString(), queryArgs.capture(), any(), eq(Post.class))).thenReturn(Page.of(new Post(), new Post()));
        Page<Post> search = repository.search(request, Pageable.unPaged());
        assertNotNull(search);
        assertEquals(2, search.size());
        // Assert query captors
        MultiValueMap<String, String> queries = queryArgs.getValue();
        String queryString = QueryStringUtils.toQueryString(queries);
        log.info("queries {}", queries);
        System.out.println(queries);
        assertEquals(1, queries.get("or").size());
        // Means that you have to make (format.minSize.gte.25 AND format.maxSize.lte.25) OR size.eq.25
        assertEquals("(filterFormats.and(minSize.gte.25,or(maxSize.lte.25,maxSize.is.null)),size.eq.25)", queries.getFirst("or"));
    }

    @Test
    void shouldSearchWithOrOnMultiple() {
        PostRequestWithAuthorOrSubject request = new PostRequestWithAuthorOrSubject();
        request.setAuthor("IA");
        request.setSubject("IA");
        ArgumentCaptor<MultiValueMap<String, String>> queryArgs = multiMapCaptor();
        when(webClient.search(anyString(), queryArgs.capture(), any(), eq(Post.class))).thenReturn(Page.of(new Post(), new Post()));

        Page<Post> search = repository.search(request, Pageable.ofSize(10));
        assertNotNull(search);
        assertEquals(2, search.size());
        // Assert query captors
        MultiValueMap<String, String> queries = queryArgs.getValue();
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
        ArgumentCaptor<MultiValueMap<String, String>> queryArgs = multiMapCaptor();
        when(webClient.search(anyString(), queryArgs.capture(), any(), eq(Post.class))).thenReturn(Page.of(new Post(), new Post()));

        Page<Post> search = repository.search(request, Pageable.ofSize(10));
        assertNotNull(search);
        assertEquals(2, search.size());
        // Assert query captors
        MultiValueMap<String, String> queries = queryArgs.getValue();
        System.out.println(queries);
        assertEquals("(dateFinValidite.gte.2023-12-04,dateFinValidite.is.null)", queries.getFirst("filtrePublication.or"));
    }

}
