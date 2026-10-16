package fr.ouestfrance.querydsl.postgrest;

import fr.ouestfrance.querydsl.postgrest.model.BulkResponse;
import fr.ouestfrance.querydsl.postgrest.model.HeaderRange;
import fr.ouestfrance.querydsl.postgrest.model.RangeResponse;
import fr.ouestfrance.querydsl.postgrest.model.exceptions.PostgrestRequestException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.lang.reflect.Type;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static fr.ouestfrance.querydsl.postgrest.ParametrizedTypeUtils.listRef;
import static fr.ouestfrance.querydsl.postgrest.ResponseUtils.toBulkResponse;

/**
 * Rest interface for querying postgrest with Spring RestClient
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class PostgrestRestClient implements PostgrestClient {

    /**
     * restClient
     */
    private final RestClient restClient;

    private final String baseUrl;

    /**
     * Default RestClient constructor
     *
     * @param restClient restClient
     * @param baseUrl    baseUrl of postgrest
     * @return concrete client
     */
    public static PostgrestRestClient of(RestClient restClient, String baseUrl) {
        return new PostgrestRestClient(restClient, baseUrl);
    }

    @Override
    public <T> RangeResponse<T> search(String resource, Map<String, List<String>> params,
                                       Map<String, List<String>> headers, Class<T> clazz) {
        ResponseEntity<List<T>> response = restClient.get()
                .uri(getUri(resource, params))
                .headers(h -> addHeaders(h, headers))
                .retrieve()
                .toEntity(listRef(clazz));

        return Optional.of(response)
                .map(ResponseEntity::getBody)
                .map(x -> {
                    HeaderRange range = ResponseUtils.getCount(response.getHeaders())
                            .orElse(null);
                    return new RangeResponse<>(x, range);
                }).orElse(new RangeResponse<>(List.of(), null));
    }

    @Override
    public <T> BulkResponse<T> post(String resource, Map<String, List<String>> params, Object value, Map<String, List<String>> headers, Class<T> clazz) {
        return toBulkResponse(exchange(HttpMethod.POST, resource, params, value, headers, clazz));
    }

    @Override
    public <T> BulkResponse<T> patch(String resource, Map<String, List<String>> params, Object value, Map<String, List<String>> headers, Class<T> clazz) {
        return toBulkResponse(exchange(HttpMethod.PATCH, resource, params, value, headers, clazz));
    }

    @Override
    public <T> BulkResponse<T> delete(String resource, Map<String, List<String>> params, Map<String, List<String>> headers, Class<T> clazz) {
        return toBulkResponse(exchange(HttpMethod.DELETE, resource, params, null, headers, clazz));
    }

    @Override
    public long count(String resource, Map<String, List<String>> map) {
        HttpHeaders headersResponse = restClient.head()
                .uri(getUri(resource, map))
                .header("Range-Unit", "items")
                .header("Prefer", "count=exact")
                .retrieve()
                .toBodilessEntity()
                .getHeaders();

        return ResponseUtils.getCount(headersResponse)
                .map(HeaderRange::getTotalElements)
                .orElse(-1L);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <V> V rpc(String rpcName, Map<String, List<String>> map, Object body, Type type) {
        try {
            RestClient.RequestBodySpec spec = restClient.post().uri(getUri(rpcName, map));
            if (body != null) {
                spec = spec.body(body);
            }
            return (V) spec.retrieve().toEntity(ParameterizedTypeReference.forType(type)).getBody();
        } catch (HttpClientErrorException.NotFound exception) {
            throw new PostgrestRequestException(rpcName, exception.getMessage(), exception, exception.getResponseBodyAsString());
        }
    }

    private <T> ResponseEntity<List<T>> exchange(HttpMethod method, String resource, Map<String, List<String>> params,
                                                 Object value, Map<String, List<String>> headers, Class<T> clazz) {
        RestClient.RequestBodySpec spec = restClient.method(method)
                .uri(getUri(resource, params))
                .headers(h -> addHeaders(h, headers));
        if (value != null) {
            spec = spec.body(value);
        }
        return spec.retrieve().toEntity(listRef(clazz));
    }

    /**
     * Copy map headers into HttpHeaders
     *
     * @param httpHeaders target
     * @param headers     map
     */
    private static void addHeaders(HttpHeaders httpHeaders, Map<String, List<String>> headers) {
        if (headers != null) {
            headers.forEach(httpHeaders::put);
        }
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
     * Get URI from resource and params
     *
     * @param resource resource
     * @param params   params
     * @return URI
     */
    protected URI getUri(String resource, Map<String, List<String>> params) {
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(baseUrl).path(resource);
        if (Objects.nonNull(params)) {
            uriBuilder = uriBuilder.queryParams(toMultiMap(params));
        }
        URI uri = uriBuilder.build().encode().toUri();
        return URI.create(uri.toASCIIString().replace("+", "%2B"));
    }

}
