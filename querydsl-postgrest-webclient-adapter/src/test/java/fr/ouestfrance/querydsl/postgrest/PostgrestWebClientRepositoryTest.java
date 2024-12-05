package fr.ouestfrance.querydsl.postgrest;

import fr.ouestfrance.querydsl.postgrest.app.*;
import fr.ouestfrance.querydsl.postgrest.criterias.Criteria;
import fr.ouestfrance.querydsl.postgrest.model.BulkResponse;
import fr.ouestfrance.querydsl.postgrest.model.Page;
import fr.ouestfrance.querydsl.postgrest.model.Pageable;
import fr.ouestfrance.querydsl.postgrest.model.Range;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.mockserver.client.MockServerClient;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.junit.jupiter.MockServerSettings;
import org.mockserver.model.HttpRequest;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static fr.ouestfrance.querydsl.postgrest.TestUtils.jsonFileResponse;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

@MockServerSettings(ports = 8007)
@Slf4j
class PostgrestWebClientRepositoryTest {

    private final PostgrestWebClient postgrestClient = PostgrestWebClient.of(WebClient.builder()
            .baseUrl("http://localhost:8007/")
            .build());
    private final PostgrestRepository<Post> repository = new PostRepository(postgrestClient);


    @Test
    void shouldCountPosts(ClientAndServer client) {
        client.reset();
        client.when(request().withPath("/posts").withQueryStringParameter("select", "count()"))
                .respond(jsonFileResponse("count_response.json"));
        long count = repository.count();
        assertEquals(300, count);
    }

    @Test
    void shouldSearchPosts(ClientAndServer client) {
        client.reset();
        client.when(request().withPath("/posts").withQueryStringParameter("select", "*,authors(*)"))
                .respond(jsonFileResponse("posts.json")
                        .withHeader("Content-Range", "0-6/300"));
        Page<Post> search = repository.search(new PostRequest(), Pageable.ofSize(6));
        System.out.println(search.getTotalElements());
        System.out.println(search.getTotalPages());
        assertEquals(300, search.getTotalElements());
        assertEquals(50, search.getTotalPages());
        assertNotNull(search);
        assertFalse(search.getData().isEmpty());
        search.getData().stream().map(Object::getClass).forEach(x -> assertEquals(Post.class, x));
    }

    @Test
    void shouldFindById(ClientAndServer client) {
        client.when(request().withPath("/posts")
                        .withQueryStringParameter("id", "eq.1")
                        .withQueryStringParameter("select", "*,authors(*)"))
                .respond(jsonFileResponse("posts.json").withHeader("Content-Range", "0-6/300"));
        Page<Post> search = repository.search(Criteria.byId("1"), Pageable.ofSize(6));
        System.out.println(search.getTotalElements());
        System.out.println(search.getTotalPages());
        assertEquals(300, search.getTotalElements());
        assertEquals(50, search.getTotalPages());
        assertNotNull(search);
        assertFalse(search.getData().isEmpty());
        search.getData().stream().map(Object::getClass).forEach(x -> assertEquals(Post.class, x));
    }

    @Test
    void shouldSearchGetByIds(ClientAndServer client) {
        client.when(request().withPath("/posts")
                        .withQueryStringParameter("id", "in.(1,2,3)")
                        .withQueryStringParameter("select", "*,authors(*)"))
                .respond(jsonFileResponse("posts.json").withHeader("Content-Range", "0-6/300"));
        Page<Post> search = repository.search(Criteria.byIds("1", "2", "3"), Pageable.ofSize(6));
        System.out.println(search.getTotalElements());
        System.out.println(search.getTotalPages());
        assertEquals(300, search.getTotalElements());
        assertEquals(50, search.getTotalPages());
        assertNotNull(search);
        assertFalse(search.getData().isEmpty());
        search.getData().stream().map(Object::getClass).forEach(x -> assertEquals(Post.class, x));
    }

    @Test
    void shouldPost(ClientAndServer client) {
        client.when(request().withPath("/posts"))
                .respond(jsonFileResponse("new_posts.json"));
        List<Post> result = repository.post(new ArrayList<>(List.of(new Post())));
        assertNotNull(result);
        result.stream().map(Object::getClass).forEach(x -> assertEquals(Post.class, x));

    }

    @Test
    void shouldPostBulk(ClientAndServer client) {
        client.when(request().withPath("/posts"))
                .respond(jsonFileResponse("new_posts.json"));
        List<Post> result = repository.post(new ArrayList<>(List.of(new Post())));
        assertNotNull(result);
        result.stream().map(Object::getClass).forEach(x -> assertEquals(Post.class, x));
    }

    @Test
    void shouldUpsert(ClientAndServer client) {
        client.when(request().withPath("/posts"))
                .respond(jsonFileResponse("new_posts.json"));
        List<Post> result = repository.upsert(new ArrayList<>(List.of(new Post())));
        assertNotNull(result);
        result.stream().map(Object::getClass).forEach(x -> assertEquals(Post.class, x));

    }

    @Test
    void shouldUpsertBulk(ClientAndServer client) {
        client.when(request().withPath("/posts"))
                .respond(jsonFileResponse("new_posts.json"));
        List<Post> result = repository.upsert(new ArrayList<>(List.of(new Post())));
        assertNotNull(result);
        result.stream().map(Object::getClass).forEach(x -> assertEquals(Post.class, x));
    }

    @Test
    void shouldPatchPost(ClientAndServer client) {
        client.when(request().withPath("/posts").withQueryStringParameter("userId", "eq.25"))
                .respond(response().withHeader("Content-Range", "0-299/300"));
        PostRequest criteria = new PostRequest();
        criteria.setUserId(25);
        BulkResponse<Post> result = repository.patch(criteria, new Post());
        assertNotNull(result);
        assertEquals(300, result.getAffectedRows());
        assertTrue(result.isEmpty());
    }

    @Test
    void shouldPatchBulkPost(ClientAndServer client) {
        client.when(request().withPath("/posts").withQueryStringParameter("userId", "eq.25"))
                .respond(response().withHeader("Content-Range", "0-299/300"));
        PostRequest criteria = new PostRequest();
        criteria.setUserId(25);
        BulkResponse<Post> result = repository.patch(criteria, new Post());
        assertNotNull(result);
        assertEquals(300, result.getAffectedRows());
        assertTrue(result.isEmpty());
    }


    @Test
    void shouldDeletePost(ClientAndServer client) {
        client.when(request().withPath("/posts").withQueryStringParameter("userId", "eq.25"))
                .respond(jsonFileResponse("posts.json"));
        PostRequest criteria = new PostRequest();
        criteria.setUserId(25);
        List<Post> result = repository.delete(criteria);
        assertNotNull(result);
        result.stream().map(Object::getClass).forEach(x -> assertEquals(Post.class, x));
    }


    @Test
    void shouldDeleteBulkPost(ClientAndServer client) {
        client.when(request().withPath("/posts").withQueryStringParameter("userId", "eq.25"))
                .respond(response().withHeader("Content-Range", "0-299/300"));
        PostRequest criteria = new PostRequest();
        criteria.setUserId(25);
        BulkResponse<Post> result = repository.delete(criteria);
        assertNotNull(result);
        assertEquals(300, result.getAffectedRows());
        assertTrue(result.isEmpty());
    }





    @Test
    void shouldValidateRangeRequest(ClientAndServer client) {
        // Call has to be
        // http://localhost:8007/posts?birthDate=gte.2021-01-01&birthDate=lte.2021-12-31&siblings=gte.1&siblings=lte.3&select=*,authors(*)
        //
        client.when(request().withPath("/posts")
                        .withQueryStringParameter("birthDate", "gte.2021-01-01", "lte.2021-12-31")
                        .withQueryStringParameter("siblings", "lte.3", "gte.1")
                        .withQueryStringParameter("select", "*,authors(*)"))
                .respond(jsonFileResponse("posts.json").withHeader("Content-Range", "0-6/300"));
        RangeRequest criteria = new RangeRequest();
        criteria.setBirthDate(Range.between(LocalDate.of(2021, 1, 1), LocalDate.of(2021, 12, 31)));
        criteria.setSiblings(Range.between(1, 3));
        Page<Post> search = repository.search(criteria, Pageable.ofSize(6));
        assertNotNull(search);

        OldWayRangeRequest oldWayRangeRequest = new OldWayRangeRequest();
        oldWayRangeRequest.setMinBirthDate(criteria.getBirthDate().getLower());
        oldWayRangeRequest.setMaxBirthDate(criteria.getBirthDate().getUpper());
        oldWayRangeRequest.setMinSiblings(criteria.getSiblings().getLower());
        oldWayRangeRequest.setMaxSiblings(criteria.getSiblings().getUpper());
        Page<Post> oldWaySearch = repository.search(oldWayRangeRequest, Pageable.ofSize(6));
        assertNotNull(oldWaySearch);
    }

    @Test
    void shouldEncodePlusChars(MockServerClient client) {
        client.when(HttpRequest.request().withPath("/posts")
                        .withQueryStringParameter("id", "eq.Romeo + Juliette")
                        .withQueryStringParameter("select", "*,authors(*)"))
                .respond(jsonFileResponse("posts.json").withHeader("Content-Range", "0-6/300"));
        Page<Post> search = repository.search(Criteria.byId("Romeo + Juliette"), Pageable.ofSize(6));
        assertNotNull(search);

        // assert uri
        URI uri = postgrestClient.getUri("/posts", Map.of("id",
                List.of("eq.Romeo + Juliette")), UriComponentsBuilder.newInstance().host("localhost").port(8007).scheme("http"));

        assertEquals("http://localhost:8007/posts?id=eq.Romeo%20%2B%20Juliette", uri.toString());
    }
}
