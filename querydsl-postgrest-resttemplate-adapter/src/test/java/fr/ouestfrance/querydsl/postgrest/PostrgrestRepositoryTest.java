package fr.ouestfrance.querydsl.postgrest;

import fr.ouestfrance.querydsl.postgrest.app.Post;
import fr.ouestfrance.querydsl.postgrest.app.PostRepository;
import fr.ouestfrance.querydsl.postgrest.app.PostRequest;
import fr.ouestfrance.querydsl.postgrest.criterias.Criteria;
import fr.ouestfrance.querydsl.postgrest.model.Page;
import fr.ouestfrance.querydsl.postgrest.model.Pageable;
import lombok.SneakyThrows;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.junit.jupiter.MockServerExtension;
import org.mockserver.junit.jupiter.MockServerSettings;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;
import org.mockserver.model.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;
import shaded_package.org.apache.commons.io.IOUtils;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockServerExtension.class)
@MockServerSettings(ports = 8007)
class PostrgrestRepositoryTest {

    private PostgrestRepository<Post> repository;

    private final ClientAndServer client;

    public PostrgrestRepositoryTest(ClientAndServer client) {
        this.client = client;
    }

    @BeforeEach
    void beforeEach() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setUriTemplateHandler(new DefaultUriBuilderFactory("http://localhost:8007"));
        restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(HttpClients.createDefault()));
        repository = new PostRepository(PostgrestRestTemplate.of(restTemplate));

        client.reset();
    }

    @Test
    void shouldSearchPosts() {
        client.when(HttpRequest.request().withPath("/posts").withQueryStringParameter("select", "*,authors(*)"))
                .respond(jsonFileResponse("posts.json").withHeader("Content-Range", "0-6/300"));
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
    void shouldSearchPostsWithoutContentRange() {
        client.when(HttpRequest.request().withPath("/posts").withQueryStringParameter("select", "*,authors(*)"))
                .respond(jsonFileResponse("posts.json"));
        Page<Post> search = repository.search(new PostRequest(), Pageable.ofSize(6));
        System.out.println(search.getTotalElements());
        System.out.println(search.getTotalPages());
        assertEquals(6, search.getTotalElements());
        assertEquals(1, search.getTotalPages());
        assertNotNull(search);
        assertFalse(search.getData().isEmpty());
        search.getData().stream().map(Object::getClass).forEach(x -> assertEquals(Post.class, x));
    }

    @Test
    void shouldFindById() {
        client.when(HttpRequest.request().withPath("/posts")
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
    void shouldSearchGetByIds() {
        client.when(HttpRequest.request().withPath("/posts")
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
    void shouldUpsertPost() {
        client.when(HttpRequest.request().withPath("/posts"))
                .respond(jsonFileResponse("new_posts.json"));
        List<Post> result = repository.upsert(new ArrayList<>(List.of(new Post())));
        assertNotNull(result);
        result.stream().map(Object::getClass).forEach(x -> assertEquals(Post.class, x));

    }


    @Test
    void shouldPatchPost() {
        client.when(HttpRequest.request().withPath("/posts").withQueryStringParameter("userId", "eq.25"))
                .respond(jsonFileResponse("posts.json"));
        PostRequest criteria = new PostRequest();
        criteria.setUserId(25);
        List<Post> result = repository.patch(criteria, new Post());
        assertNotNull(result);
        result.stream().map(Object::getClass).forEach(x -> assertEquals(Post.class, x));
    }

    @Test
    void shouldDeletePost() {
        client.when(HttpRequest.request().withPath("/posts").withQueryStringParameter("userId", "eq.25"))
                .respond(jsonFileResponse("posts.json"));
        PostRequest criteria = new PostRequest();
        criteria.setUserId(25);
        List<Post> result = repository.delete(criteria);
        assertNotNull(result);
        result.stream().map(Object::getClass).forEach(x -> assertEquals(Post.class, x));
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
