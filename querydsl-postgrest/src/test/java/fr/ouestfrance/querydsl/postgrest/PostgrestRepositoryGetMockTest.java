package fr.ouestfrance.querydsl.postgrest;

import fr.ouestfrance.querydsl.postgrest.app.*;
import fr.ouestfrance.querydsl.postgrest.criterias.Criteria;
import fr.ouestfrance.querydsl.postgrest.model.*;
import fr.ouestfrance.querydsl.postgrest.model.exceptions.PostgrestRequestException;
import fr.ouestfrance.querydsl.postgrest.utils.QueryStringUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@Slf4j
class PostgrestRepositoryGetMockTest extends AbstractRepositoryMockTest {


    @Mock
    private PostgrestClient webClient;

    private PostgrestRepository<Post> repository;

    private PostgrestRepository<Post> repositoryLight;

    @BeforeEach
    void beforeEach() {
        repository = new PostRepository(webClient);
        repositoryLight = new PostLightRepository(webClient);
    }

    @Test
    void shouldSearchAllPosts() {
        when(webClient.search(anyString(), any(), any(), eq(Post.class))).thenReturn(RangeResponse.of(new Post(), new Post()));
        Page<Post> search = repository.search(null);
        assertNotNull(search);
        assertNotNull(search.iterator());
        assertEquals(2, search.size());
        assertFalse(search.hasNext());
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
        ArgumentCaptor<Map<String, List<String>>> queryArgs = multiMapCaptor();
        ArgumentCaptor<Map<String, List<String>>> headerArgs = multiMapCaptor();
        when(webClient.search(anyString(), queryArgs.capture(), headerArgs.capture(), eq(Post.class))).thenReturn(RangeResponse.of(new Post(), new Post()));

        Page<Post> search = repository.search(request, Pageable.ofSize(10, Sort.by(Sort.Order.asc("id"), Sort.Order.desc("title").nullsFirst(), Sort.Order.asc("author").nullsLast())));
        assertNotNull(search);
        assertEquals(2, search.size());
        // Assert query captors
        Map<String, List<String>> queries = queryArgs.getValue();
        log.info("queries {}", queries);
        assertEquals("eq.1", queries.get("userId").stream().findFirst().orElseThrow());
        assertEquals("neq.1", queries.get("id").stream().findFirst().orElseThrow());
        assertEquals("lte.2023-11-10", queries.get("startDate").stream().findFirst().orElseThrow());
        assertEquals("(endDate.gte.2023-11-10,endDate.is.null)", queries.get("or").stream().findFirst().orElseThrow());
        assertEquals("like.Test*", queries.get("title").stream().findFirst().orElseThrow());
        assertEquals("id,title.desc.nullsfirst,author.nullslast", queries.get("order").stream().findFirst().orElseThrow());
        assertEquals("*,authors(*)", queries.get("select").stream().findFirst().orElseThrow());
        assertEquals(2, queries.get("status").size());
        // Assert headers captors
        Map<String, List<String>> value = headerArgs.getValue();
        assertEquals("0-9", value.get("Range").stream().findFirst().orElseThrow());
        assertEquals("items", value.get("Range-Unit").stream().findFirst().orElseThrow());
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
    void shouldSearchWithLightRepository() {
        ArgumentCaptor<Map<String, List<String>>> queryArgs = multiMapCaptor();
        ArgumentCaptor<Map<String, List<String>>> headerArgs = multiMapCaptor();
        when(webClient.search(anyString(), queryArgs.capture(), headerArgs.capture(), eq(Post.class))).thenReturn(RangeResponse.of(new Post(), new Post()));
        repositoryLight.search(new PostRequest(), Pageable.ofSize(10));

        // Assert query captors
        Map<String, List<String>> queries = queryArgs.getValue();
        assertEquals("userId,id,title,body", queries.get("select").stream().findFirst().orElseThrow());

    }

    @Test
    void shouldFindById() {
        ArgumentCaptor<Map<String, List<String>>> queryArgs = multiMapCaptor();
        when(webClient.search(anyString(), queryArgs.capture(), any(), eq(Post.class))).thenReturn(RangeResponse.of(new Post()));
        Page<Post> search = repository.search(Criteria.byId("1"), Pageable.ofSize(6));
        // Assert query captors
        Map<String, List<String>> queries = queryArgs.getValue();
        assertEquals("eq.1", queries.get("id").stream().findFirst().orElseThrow());
        log.info("queries {}", queries);
        assertNotNull(search);
        assertFalse(search.getData().isEmpty());
        search.getData().stream().map(Object::getClass).forEach(x -> assertEquals(Post.class, x));
    }

    @Test
    void shouldFindByIds() {
        ArgumentCaptor<Map<String, List<String>>> queryArgs = multiMapCaptor();
        when(webClient.search(anyString(), queryArgs.capture(), any(), eq(Post.class))).thenReturn(RangeResponse.of(new Post()));
        Page<Post> search = repository.search(Criteria.byIds("1", "2", "3"), Pageable.ofSize(6));
        // Assert query captors
        Map<String, List<String>> queries = queryArgs.getValue();
        assertEquals("in.(1,2,3)", queries.get("id").stream().findFirst().orElseThrow());
        log.info("queries {}", queries);
        assertNotNull(search);
        assertFalse(search.getData().isEmpty());
        search.getData().stream().map(Object::getClass).forEach(x -> assertEquals(Post.class, x));
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
        when(webClient.search(anyString(), any(), any(), eq(Post.class))).thenReturn(new RangeResponse<>(List.of(new Post()),new HeaderRange(0,1,2)));

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
        ArgumentCaptor<Map<String, List<String>>> queryArgs = multiMapCaptor();
        ArgumentCaptor<Map<String, List<String>>> headerArgs = multiMapCaptor();
        when(webClient.search(anyString(), queryArgs.capture(), headerArgs.capture(), eq(Post.class))).thenReturn(new RangeResponse<>(List.of(new Post()),  new HeaderRange(0,2, 2)));

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
        ArgumentCaptor<Map<String, List<String>>> queryArgs = multiMapCaptor();
        ArgumentCaptor<Map<String, List<String>>> headerArgs = multiMapCaptor();
        when(webClient.search(anyString(), queryArgs.capture(), headerArgs.capture(), eq(Post.class))).thenReturn(RangeResponse.of(new Post(), new Post()));

        Page<Post> search = repository.search(request, Pageable.ofSize(10));
        assertNotNull(search);
        assertEquals(2, search.size());
        // Assert query captors
        Map<String, List<String>> queries = queryArgs.getValue();
        log.info("queries {}", queries);
        assertNull(queries.get("order"));
        // Assert headers captors
        Map<String, List<String>> value = headerArgs.getValue();
        assertEquals("0-9", value.get("Range").stream().findFirst().orElseThrow());
        assertEquals("items", value.get("Range-Unit").stream().findFirst().orElseThrow());
    }

    @Test
    void shouldRaiseExceptionOnMultipleOne() {
        when(webClient.search(anyString(), any(), any(), eq(Post.class))).thenReturn(RangeResponse.of(new Post(), new Post()));
        assertThrows(PostgrestRequestException.class, () -> repository.findOne(null));
    }

    @Test
    void shouldFindOne() {
        when(webClient.search(anyString(), any(), any(), eq(Post.class))).thenReturn(RangeResponse.of(new Post()));
        Optional<Post> one = repository.findOne(null);
        assertNotNull(one);
        assertTrue(one.isPresent());
    }


    @Test
    void shouldFindEmptyOne() {
        when(webClient.search(anyString(), any(), any(), eq(Post.class))).thenReturn(RangeResponse.of());
        Optional<Post> one = repository.findOne(null);
        assertNotNull(one);
        assertTrue(one.isEmpty());
    }


    @Test
    void shouldGetOne() {
        when(webClient.search(anyString(), any(), any(), eq(Post.class))).thenReturn(RangeResponse.of(new Post()));
        Post one = repository.getOne(null);
        assertNotNull(one);
    }

    @Test
    void shouldRaiseExceptionOnEmptyGetOne() {
        when(webClient.search(anyString(), any(), any(), eq(Post.class))).thenReturn(RangeResponse.of());
        assertThrows(PostgrestRequestException.class, () -> repository.getOne(null));
    }

    @Test
    void shouldSearchWithJoin() {
        PostRequestWithSize request = new PostRequestWithSize();
        request.setSize("25");
        ArgumentCaptor<Map<String, List<String>>> queryArgs = multiMapCaptor();
        when(webClient.search(anyString(), queryArgs.capture(), any(), eq(Post.class))).thenReturn(RangeResponse.of(new Post(), new Post()));
        Page<Post> search = repository.search(request, Pageable.unPaged());
        assertNotNull(search);
        assertEquals(2, search.size());
        // Assert query captors
        Map<String, List<String>> queries = queryArgs.getValue();
        String queryString = QueryStringUtils.toQueryString(queries);
        log.info("queryString {}", queryString);
        log.info("queries {}", queries);
        System.out.println(queries);
        assertEquals(1, queries.get("or").size());
        // Means that you have to make (format.minSize.gte.25 AND format.maxSize.lte.25) OR size.eq.25
        assertEquals("(filterFormats.and(minSize.gte.25,or(maxSize.lte.25,maxSize.is.null)),size.eq.25)", queries.get("or").stream().findFirst().orElseThrow());
    }

    @Test
    void shouldSearchWithOrOnMultiple() {
        PostRequestWithAuthorOrSubject request = new PostRequestWithAuthorOrSubject();
        request.setAuthor("IA");
        request.setSubject("IA");
        ArgumentCaptor<Map<String, List<String>>> queryArgs = multiMapCaptor();
        when(webClient.search(anyString(), queryArgs.capture(), any(), eq(Post.class))).thenReturn(RangeResponse.of(new Post(), new Post()));

        Page<Post> search = repository.search(request, Pageable.ofSize(10));
        assertNotNull(search);
        assertEquals(2, search.size());
        // Assert query captors
        Map<String, List<String>> queries = queryArgs.getValue();
        String queryString = QueryStringUtils.toQueryString(queries);
        System.out.println("queries : " + queryString);
        log.info("queries {}", queries);
        assertEquals("(subject.eq.IA,author.name.eq.IA)", queries.get("or").stream().findFirst().orElseThrow());
        String[] selects = queries.get("select").stream().findFirst().orElseThrow().split(",");
        assertEquals(3, selects.length);
        assertTrue(Arrays.asList(selects).contains("author:authors!inner(name)"));
    }

    @Test
    void testPublicationRequest() {
        PublicationRequest request = new PublicationRequest();
        request.setCode("25");
        request.setPortee("['DEPARTEMENT']");
        request.setDateValide(LocalDate.of(2023, 12, 4));
        ArgumentCaptor<Map<String, List<String>>> queryArgs = multiMapCaptor();
        when(webClient.search(anyString(), queryArgs.capture(), any(), eq(Post.class))).thenReturn(RangeResponse.of(new Post(), new Post()));

        Page<Post> search = repository.search(request, Pageable.ofSize(10));
        assertNotNull(search);
        assertEquals(2, search.size());
        // Assert query captors
        Map<String, List<String>> queries = queryArgs.getValue();
        System.out.println(queries);
        assertEquals("(dateFinValidite.gte.2023-12-04,dateFinValidite.is.null)", queries.get("filtrePublication.or").stream().findFirst().orElseThrow());
    }

    @Test
    void testRangeRequest() {
        RangeRequest request = new RangeRequest();
        request.setBirthDate(Range.between(LocalDate.of(2023, 12, 4), LocalDate.of(2023, 12, 5)));
        request.setSiblings(Range.between(1, 3));
        ArgumentCaptor<Map<String, List<String>>> queryArgs = multiMapCaptor();
        when(webClient.search(anyString(), queryArgs.capture(), any(), eq(Post.class))).thenReturn(RangeResponse.of(new Post(), new Post()));

        Page<Post> search = repository.search(request, Pageable.ofSize(10));
        assertNotNull(search);
        assertEquals(2, search.size());
        // Assert query captors
        Map<String, List<String>> queries = queryArgs.getValue();
        System.out.println(queries);
        assertEquals("gte.2023-12-04", queries.get("birthDate").get(0));
        assertEquals("lte.2023-12-05", queries.get("birthDate").get(1));
        assertEquals("gte.1", queries.get("siblings").get(0));
        assertEquals("lte.3", queries.get("siblings").get(1));
    }

}
