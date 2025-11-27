package fr.ouestfrance.querydsl.postgrest;

import fr.ouestfrance.querydsl.postgrest.model.BulkResponse;
import fr.ouestfrance.querydsl.postgrest.model.HeaderRange;
import fr.ouestfrance.querydsl.postgrest.model.RangeResponse;
import fr.ouestfrance.querydsl.postgrest.model.exceptions.PostgrestRequestException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.util.UriBuilder;

import java.lang.reflect.Type;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
        ResponseEntity<List<T>> response = webClient.get().uri(uriBuilder -> getUri(resource, params, uriBuilder)).headers(httpHeaders ->
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
    public long count(String resource, Map<String, List<String>> params) {
        ResponseEntity<Void> response = webClient.head()
                .uri(uriBuilder -> getUri(resource, params, uriBuilder))
                .header("Range-Unit", "items")
                .header("Prefer", "count=exact")
                .retrieve()
                .toBodilessEntity()
                .block();

        return Optional.ofNullable(response)
                .map(ResponseEntity::getHeaders)
                .flatMap(ResponseUtils::getCount)
                .map(HeaderRange::getTotalElements)
                .orElse(-1L);

    }

    @Override
    public <V> V rpc(String rpcName, Map<String, List<String>> params, Object body, Type clazz) {
        WebClient.RequestBodySpec request = webClient.post().uri(uriBuilder -> getUri(rpcName, params, uriBuilder));
        Optional.ofNullable(body).ifPresent(request::bodyValue);
        Object result = request
                .retrieve()
                .bodyToMono(ParameterizedTypeReference.forType(clazz))
                // On not found raise postgrestRequestException with body
                .onErrorMap(WebClientResponseException.NotFound.class, e -> new PostgrestRequestException(rpcName, e.getMessage(), e, e.getResponseBodyAsString()))
                .block();
        if (result != null) {
            return (V) result;
        }
        return null;
    }


    @Override
    public <T> BulkResponse<T> post(String resource, Map<String, List<String>> params, Object value, Map<String, List<String>> headers, Class<T> clazz) {
        ResponseEntity<List<T>> response = webClient.post().uri(uriBuilder -> getUri(resource, params, uriBuilder)).headers(httpHeaders -> safeAdd(headers, httpHeaders))
                .bodyValue(value)
                .retrieve()
                .toEntity(listRef(clazz))
                .block();
        return toBulkResponse(response);
    }


    @Override
    public <T> BulkResponse<T> patch(String resource, Map<String, List<String>> params, Object value, Map<String, List<String>> headers, Class<T> clazz) {
        ResponseEntity<List<T>> response = webClient.patch().uri(uriBuilder -> getUri(resource, params, uriBuilder))
                .bodyValue(value)
                .headers(httpHeaders -> safeAdd(headers, httpHeaders))
                .retrieve()
                .toEntity(listRef(clazz)).block();
        return toBulkResponse(response);
    }

    @Override
    public <T> BulkResponse<T> delete(String resource, Map<String, List<String>> params, Map<String, List<String>> headers, Class<T> clazz) {
        ResponseEntity<List<T>> response = webClient.delete().uri(uriBuilder -> getUri(resource, params, uriBuilder))
                .headers(httpHeaders -> safeAdd(headers, httpHeaders))
                .retrieve()
                .toEntity(listRef(clazz)).block();
        return toBulkResponse(response);
    }


    protected  URI getUri(String resource, Map<String, List<String>> params, UriBuilder uriBuilder) {
        uriBuilder.path(resource);
        uriBuilder.queryParams(toMultiMap(params));
        return URI.create(uriBuilder.build().toASCIIString().replace("+", "%2B"));
    }

    /**
     * Convert map to MultiValueMap
     *
     * @param params map
     * @return MultiValueMap
     */
    private static MultiValueMap<String, String> toMultiMap(Map<String, List<String>> params) {
        return new LinkedMultiValueMap<>(params);
    }

    /**
     * Safe add headers to httpHeaders
     *
     * @param headers     headers
     * @param httpHeaders httpHeaders
     */
    private static void safeAdd(Map<String, List<String>> headers, HttpHeaders httpHeaders) {
        if (headers != null) {
            headers.forEach(httpHeaders::put);
        }
        // Add contentType with default on call if webclient default is not set
        httpHeaders.put(CONTENT_TYPE, List.of(MediaType.APPLICATION_JSON_VALUE));
    }
}
