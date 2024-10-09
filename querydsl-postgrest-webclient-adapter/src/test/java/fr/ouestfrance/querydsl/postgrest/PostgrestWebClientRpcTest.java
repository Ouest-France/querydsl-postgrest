package fr.ouestfrance.querydsl.postgrest;

import fr.ouestfrance.querydsl.postgrest.app.Post;
import fr.ouestfrance.querydsl.postgrest.app.PostRequestWithSelect;
import fr.ouestfrance.querydsl.postgrest.model.exceptions.PostgrestRequestException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockserver.client.MockServerClient;
import org.mockserver.junit.jupiter.MockServerSettings;
import org.mockserver.model.HttpRequest;
import org.springframework.web.reactive.function.client.WebClient;

import static fr.ouestfrance.querydsl.postgrest.TestUtils.jsonResponse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@MockServerSettings(ports = 8007)
@Slf4j
class PostgrestWebClientRpcTest {

    private final PostgrestWebClient client = PostgrestWebClient.of(WebClient.builder()
            .baseUrl("http://localhost:8007/")
            .build());
    private final PostgrestRpcClient rpcClient = new PostgrestRpcClient(client);

    @BeforeEach
    void beforeEach(MockServerClient client) {
        client.reset();
    }

    @Test
    void shouldCallRpc(MockServerClient client) {
        client.when(HttpRequest.request().withPath("/rpc/testV1"))
                .respond(jsonResponse("""
                        {"id": 1, "title": "test"}
                        """));
        Post result = rpcClient.executeRpc("testV1", Post.class).orElse(null);
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
        Post result = rpcClient.executeRpc("testV1", criteria, null, Post.class).orElse(null);
        assertNotNull(result);
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
        Post[] result = rpcClient.executeRpc("testV1", criteria, null, Post[].class).orElse(null);
        assertNotNull(result);
    }


    @Test
    void shouldRaiseExceptionOn404(MockServerClient client) {
        client.when(HttpRequest.request().withPath("/rpc/testV1"))
                .respond(jsonResponse("""
                        {
                            "code":"PGRST202",
                            "details":"Searched for the function public_repository_depositaire.testV1 with parameters coordinates, type or with a single unnamed json/jsonb parameter, but no matches were found in the schema cache.",
                            "hint":null,
                            "message":"Could not find the function public_repository_depositaire.testV1(coordinates, type) in the schema cache"
                        }
                        """).withStatusCode(404));
        PostgrestRequestException exception = assertThrows(PostgrestRequestException.class, () -> rpcClient.executeRpc("testV1", Post.class));
        assertNotNull(exception);
        assertNotNull(exception.getResponseBody());
    }

}
