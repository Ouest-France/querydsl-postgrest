package fr.ouestfrance.querydsl.postgrest;

import fr.ouestfrance.querydsl.postgrest.app.Post;
import fr.ouestfrance.querydsl.postgrest.app.PostRepository;
import fr.ouestfrance.querydsl.postgrest.app.PostRequest;
import fr.ouestfrance.querydsl.postgrest.criterias.Criteria;
import fr.ouestfrance.querydsl.postgrest.model.BulkResponse;
import fr.ouestfrance.querydsl.postgrest.model.Page;
import fr.ouestfrance.querydsl.postgrest.model.Pageable;
import lombok.SneakyThrows;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockserver.client.MockServerClient;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.junit.jupiter.MockServerSettings;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;
import org.mockserver.model.MediaType;
import org.mockserver.verify.Verification;
import org.mockserver.verify.VerificationTimes;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import shaded_package.org.apache.commons.io.IOUtils;

import java.net.URI;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@MockServerSettings(ports = 8007)
class PostgrestRestTemplateRepositoryTest {

    private PostgrestRepository<Post> repository;
    private PostgrestRpcClient rpcClient;
    private PostgrestRestTemplate postgrestRestTemplate;

    @BeforeEach
    void beforeEach(MockServerClient client) {
        client.reset();
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(HttpClients.createDefault()));
        postgrestRestTemplate = PostgrestRestTemplate.of(restTemplate, "http://localhost:8007");
        repository = new PostRepository(postgrestRestTemplate);
        rpcClient = new PostgrestRpcClient(postgrestRestTemplate);
    }

    @Test
    void shouldSearchPosts(MockServerClient client) {
        client.when(HttpRequest.request().withPath("/posts").withQueryStringParameter("select", "*,authors(*)"))
                .respond(jsonFileResponse("posts.json").withHeader("Content-Range", "0-6/300"));
        Page<Post> search = repository.search(new PostRequest(), Pageable.ofSize(6));
        assertEquals(300, search.getTotalElements());
        assertEquals(50, search.getTotalPages());
        assertNotNull(search);
        assertFalse(search.getData().isEmpty());
        search.getData().stream().map(Object::getClass).forEach(x -> assertEquals(Post.class, x));
    }

    @Test
    void shouldCountPosts(ClientAndServer client) {
        client.when(HttpRequest.request().withPath("/posts").withQueryStringParameter("select", "count()"))
                .respond(jsonFileResponse("count_response.json"));
        long count = repository.count(new PostRequest());
        assertEquals(300, count);
    }

    @Test
    void shouldSearchPostsWithoutContentRange(MockServerClient client) {
        client.when(HttpRequest.request().withPath("/posts").withQueryStringParameter("select", "*,authors(*)"))
                .respond(jsonFileResponse("posts.json"));
        Page<Post> search = repository.search(new PostRequest(), Pageable.ofSize(6));
        assertEquals(6, search.getTotalElements());
        assertEquals(1, search.getTotalPages());
        assertNotNull(search);
        assertFalse(search.getData().isEmpty());
        search.getData().stream().map(Object::getClass).forEach(x -> assertEquals(Post.class, x));
    }

    @Test
    void shouldFindById(MockServerClient client) {
        client.when(HttpRequest.request().withPath("/posts")
                        .withQueryStringParameter("id", "eq.1")
                        .withQueryStringParameter("select", "*,authors(*)"))
                .respond(jsonFileResponse("posts.json").withHeader("Content-Range", "0-6/300"));
        Page<Post> search = repository.search(Criteria.byId("1"), Pageable.ofSize(6));
        assertEquals(300, search.getTotalElements());
        assertEquals(50, search.getTotalPages());
        assertNotNull(search);
        assertFalse(search.getData().isEmpty());
        search.getData().stream().map(Object::getClass).forEach(x -> assertEquals(Post.class, x));
    }

    @Test
    void shouldSearchGetByIds(MockServerClient client) {
        client.when(HttpRequest.request().withPath("/posts")
                        .withQueryStringParameter("id", "in.(1,2,3)")
                        .withQueryStringParameter("select", "*,authors(*)"))
                .respond(jsonFileResponse("posts.json").withHeader("Content-Range", "0-6/300"));
        Page<Post> search = repository.search(Criteria.byIds("1", "2", "3"), Pageable.ofSize(6));
        assertEquals(300, search.getTotalElements());
        assertEquals(50, search.getTotalPages());
        assertNotNull(search);
        assertFalse(search.getData().isEmpty());
        search.getData().stream().map(Object::getClass).forEach(x -> assertEquals(Post.class, x));
    }

    @Test
    void shouldPost(MockServerClient client) {
        client.when(HttpRequest.request().withPath("/posts"))
                .respond(jsonFileResponse("new_posts.json"));
        List<Post> result = repository.post(new ArrayList<>(List.of(new Post())));
        assertNotNull(result);
        result.stream().map(Object::getClass).forEach(x -> assertEquals(Post.class, x));
    }

    @Test
    void shouldBulkPost(MockServerClient client) {
        client.when(HttpRequest.request().withPath("/posts"))
                .respond(HttpResponse.response().withHeader("Content-Range", "0-299/300"));
        BulkResponse<Post> result = repository.post(new ArrayList<>(List.of(new Post())));
        assertNotNull(result);
        assertEquals(300L, result.getAffectedRows());
        assertTrue(result.isEmpty());
    }

    @Test
    void shouldUpsert(MockServerClient client) {
        client.when(HttpRequest.request().withPath("/posts"))
                .respond(jsonFileResponse("new_posts.json"));
        List<Post> result = repository.upsert(new ArrayList<>(List.of(new Post())));
        assertNotNull(result);
        result.stream().map(Object::getClass).forEach(x -> assertEquals(Post.class, x));
    }

    @Test
    void shouldBulkUpsert(MockServerClient client) {
        client.when(HttpRequest.request().withPath("/posts"))
                .respond(HttpResponse.response().withHeader("Content-Range", "0-299/300"));
        BulkResponse<Post> result = repository.upsert(new ArrayList<>(List.of(new Post())));
        assertNotNull(result);
        assertEquals(300L, result.getAffectedRows());
        assertTrue(result.isEmpty());
    }

    @Test
    void shouldPatchPost(MockServerClient client) {
        client.when(HttpRequest.request().withPath("/posts").withQueryStringParameter("userId", "eq.25"))
                .respond(jsonFileResponse("posts.json"));
        PostRequest criteria = new PostRequest();
        criteria.setUserId(25);
        List<Post> result = repository.patch(criteria, new Post());
        assertNotNull(result);
        result.stream().map(Object::getClass).forEach(x -> assertEquals(Post.class, x));
    }

    @Test
    void shouldPatchBulkPost(MockServerClient client) {
        client.when(HttpRequest.request().withPath("/posts").withQueryStringParameter("userId", "eq.25"))
                .respond(HttpResponse.response().withHeader("Content-Range", "0-299/300"));
        PostRequest criteria = new PostRequest();
        criteria.setUserId(25);
        BulkResponse<Post> result = repository.patch(criteria, new Post());
        assertNotNull(result);
        assertEquals(300L, result.getAffectedRows());
        assertTrue(result.isEmpty());
    }


    @Test
    void shouldDeletePost(MockServerClient client) {
        client.when(HttpRequest.request().withPath("/posts").withQueryStringParameter("userId", "eq.25"))
                .respond(jsonFileResponse("posts.json"));
        PostRequest criteria = new PostRequest();
        criteria.setUserId(25);
        List<Post> result = repository.delete(criteria);
        assertNotNull(result);
        result.stream().map(Object::getClass).forEach(x -> assertEquals(Post.class, x));
    }

    @Test
    void shouldDeleteBulkPost(MockServerClient client) {
        client.when(HttpRequest.request().withPath("/posts").withQueryStringParameter("userId", "eq.25"))
                .respond(HttpResponse.response().withHeader("Content-Range", "0-299/300"));
        PostRequest criteria = new PostRequest();
        criteria.setUserId(25);
        BulkResponse<Post> result = repository.delete(criteria);
        assertNotNull(result);
        assertEquals(300L, result.getAffectedRows());
        assertTrue(result.isEmpty());
    }


    @Test
    void shouldCallRpc(MockServerClient client) {
        client.when(HttpRequest.request().withPath("/rpc/testV1"))
                .respond(jsonResponse("""
                        {"id": 1, "title": "test"}
                        """));

        Post result = rpcClient.executeRpc("testV1", null, Post.class).orElse(null);
        assertNotNull(result);
    }


    @Test
    void shouldCallRpcResultIsList(MockServerClient client) {
        client.when(HttpRequest.request().withPath("/rpc/testV1"))
                .respond(jsonResponse("""
                        [{"id": 1, "title": "test"}]
                        """));

        Post[] result = rpcClient.executeRpc("testV1", null, Post[].class).orElse(null);
        assertNotNull(result);
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
        URI uri = postgrestRestTemplate.getUri("/posts", Map.of("id",
                List.of("eq.Romeo + Juliette")));

        assertEquals("http://localhost:8007/posts?id=eq.Romeo%20%2B%20Juliette", uri.toString());
    }

    private HttpResponse jsonResponse(String content) {
        return HttpResponse.response().withContentType(MediaType.APPLICATION_JSON)
                .withBody(content);
    }

    private HttpResponse jsonFileResponse(String resourceFileName) {
        return HttpResponse.response().withContentType(MediaType.APPLICATION_JSON)
                .withBody(jsonOf(resourceFileName));
    }

    @SneakyThrows
    private String jsonOf(String name) {
        return IOUtils.resourceToString(name, Charset.defaultCharset(), getClass().getClassLoader());
    }
}
