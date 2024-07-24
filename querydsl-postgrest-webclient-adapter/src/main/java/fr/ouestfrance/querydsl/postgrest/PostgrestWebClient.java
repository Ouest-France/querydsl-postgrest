package fr.ouestfrance.querydsl.postgrest;

import fr.ouestfrance.querydsl.postgrest.model.BulkResponse;
import fr.ouestfrance.querydsl.postgrest.model.CountItem;
import fr.ouestfrance.querydsl.postgrest.model.HeaderRange;
import fr.ouestfrance.querydsl.postgrest.model.RangeResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static fr.ouestfrance.querydsl.postgrest.ParametrizedTypeUtils.listRef;
import static fr.ouestfrance.querydsl.postgrest.ResponseUtils.toBulkResponse;

/**
 * Rest interface for querying postgrest
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class PostgrestWebClient implements PostgrestClient {

    /**
     * Content type
     */
    private static final String CONTENT_TYPE = "Content-Type";

    /**
     * webClient
     */
    private final WebClient webClient;

    /**
     * Postgrest webclient adapter
     *
     * @param webClient webClient
     * @return PostgrestWebClient implementation
     */
    public static PostgrestWebClient of(WebClient webClient) {
        return new PostgrestWebClient(webClient);
    }

    @Override
    public <T> RangeResponse<T> search(String resource, Map<String, List<String>> params,
                                       Map<String, List<String>> headers, Class<T> clazz) {
        ResponseEntity<List<T>> response = webClient.get().uri(uriBuilder -> {
                    uriBuilder.path(resource);
                    uriBuilder.queryParams(toMultiMap(params));
                    return uriBuilder.build();
                }).headers(httpHeaders ->
                        safeAdd(headers, httpHeaders)
                )
                .retrieve()
                .toEntity(listRef(clazz))
                .block();
        // Retrieve result headers
        return Optional.ofNullable(response)
                .map(HttpEntity::getBody)
                .map(x -> {
                    HeaderRange range = ResponseUtils.getCount(response.getHeaders())
                            .orElse(null);
                    return new RangeResponse<>(x, range);
                }).orElse(new RangeResponse<>(List.of(), null));
    }

    @Override
    public List<CountItem> count(String resource, Map<String, List<String>> params) {
        ResponseEntity<List<CountItem>> response = webClient.get().uri(uriBuilder -> {
                    uriBuilder.path(resource);
                    uriBuilder.queryParams(toMultiMap(params));
                    return uriBuilder.build();
                })
                .retrieve()
                .toEntity(listRef(CountItem.class))
                .block();
        return Optional.ofNullable(response).map(HttpEntity::getBody).orElse(List.of());
    }


    @Override
    public <T> BulkResponse<T> post(String resource, Map<String, List<String>> params, List<Object> value, Map<String, List<String>> headers, Class<T> clazz) {
        ResponseEntity<List<T>> response = webClient.post().uri(uriBuilder -> {
                    uriBuilder.path(resource);
                    uriBuilder.queryParams(toMultiMap(params));
                    return uriBuilder.build();
                }).headers(httpHeaders -> safeAdd(headers, httpHeaders))
                .bodyValue(value)
                .retrieve()
                .toEntity(listRef(clazz))
                .block();
        return toBulkResponse(response);
    }


    @Override
    public <T> BulkResponse<T> patch(String resource, Map<String, List<String>> params, Object value, Map<String, List<String>> headers, Class<T> clazz) {
        ResponseEntity<List<T>> response = webClient.patch().uri(uriBuilder -> {
                    uriBuilder.path(resource);
                    uriBuilder.queryParams(toMultiMap(params));
                    return uriBuilder.build();
                })
                .bodyValue(value)
                .headers(httpHeaders -> safeAdd(headers, httpHeaders))
                .retrieve()
                .toEntity(listRef(clazz)).block();
        return toBulkResponse(response);
    }

    @Override
    public <T> BulkResponse<T> delete(String resource, Map<String, List<String>> params, Map<String, List<String>> headers, Class<T> clazz) {
        ResponseEntity<List<T>> response = webClient.delete().uri(uriBuilder -> {
                    uriBuilder.path(resource);
                    uriBuilder.queryParams(toMultiMap(params));
                    return uriBuilder.build();
                }).headers(httpHeaders -> safeAdd(headers, httpHeaders))
                .retrieve()
                .toEntity(listRef(clazz)).block();
        return toBulkResponse(response);
    }


    /**
     * Convert map to MultiValueMap
     * @param params map
     * @return MultiValueMap
     */
    private static MultiValueMap<String, String> toMultiMap(Map<String, List<String>> params) {
        return new LinkedMultiValueMap<>(params);
    }

    /**
     * Safe add headers to httpHeaders
     * @param headers headers
     * @param httpHeaders httpHeaders
     */
    private static void safeAdd(Map<String, List<String>> headers, HttpHeaders httpHeaders) {
        Optional.ofNullable(headers)
                .map(PostgrestWebClient::toMultiMap).ifPresent(httpHeaders::addAll);
        // Add contentType with default on call if webclient default is not set
        httpHeaders.put(CONTENT_TYPE, List.of(MediaType.APPLICATION_JSON_VALUE));
    }
}
