package fr.ouestfrance.querydsl.postgrest;

import fr.ouestfrance.querydsl.postgrest.app.Post;
import fr.ouestfrance.querydsl.postgrest.app.PostRequestWithSelect;
import org.apache.commons.lang3.reflect.TypeUtils;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockserver.client.MockServerClient;
import org.mockserver.junit.jupiter.MockServerSettings;
import org.mockserver.model.HttpRequest;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static fr.ouestfrance.querydsl.postgrest.TestUtils.jsonResponse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@MockServerSettings(ports = 8007)
class PostgrestRpcTest {

    private PostgrestRpcClient rpcClient;

    @BeforeEach
    void beforeEach(MockServerClient client) {
        client.reset();
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(HttpClients.createDefault()));
        PostgrestRestTemplate postgrestRestTemplate = PostgrestRestTemplate.of(restTemplate, "http://localhost:8007");
        rpcClient = new PostgrestRpcClient(postgrestRestTemplate);
    }

    @Test
    void shouldCallRpc(MockServerClient client) {
        client.when(HttpRequest.request().withPath("/rpc/testV1"))
                .respond(jsonResponse("""
                        {"id": 1, "title": "test"}
                        """));
        Post result = rpcClient.executeRpc("testV1",  Post.class);
        assertNotNull(result);
    }

    @Test
    void shouldCallRpcWithCriteria(MockServerClient client) {
        client.when(HttpRequest.request().withPath("/rpc/testV1")
                        .withQueryStringParameter("userId", "eq.1")
                        .withQueryStringParameter("select", "title,userId"))
                .respond(jsonResponse("""
                        {"id": 1, "title": "test"}
                        """));

        PostRequestWithSelect criteria = new PostRequestWithSelect();
        criteria.setUserId(1);
        Post result = rpcClient.executeRpc("testV1", criteria,null, Post.class);
        assertNotNull(result);
        System.out.println(result);
    }

    @Test
    void shouldCallRpcWithCriteriaResultIsList(MockServerClient client) {
        client.when(HttpRequest.request().withPath("/rpc/testV1")
                        .withQueryStringParameter("userId", "eq.1")
                        .withQueryStringParameter("select", "title,userId"))
                .respond(jsonResponse("""
                        [{"id": 1, "title": "test"}]
                        """));

        PostRequestWithSelect criteria = new PostRequestWithSelect();
        criteria.setUserId(1);
        List<Post> result = rpcClient.executeRpc("testV1", criteria, null, TypeUtils.parameterize(List.class, Post.class));
        assertNotNull(result);
        System.out.println(result);
    }
}
