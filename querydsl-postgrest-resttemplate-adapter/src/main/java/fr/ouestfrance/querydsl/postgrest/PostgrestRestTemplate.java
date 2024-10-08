package fr.ouestfrance.querydsl.postgrest;

import fr.ouestfrance.querydsl.postgrest.model.BulkResponse;
import fr.ouestfrance.querydsl.postgrest.model.CountItem;
import fr.ouestfrance.querydsl.postgrest.model.HeaderRange;
import fr.ouestfrance.querydsl.postgrest.model.RangeResponse;
import fr.ouestfrance.querydsl.postgrest.model.exceptions.PostgrestRequestException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;
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
 * Rest interface for querying postgrest
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class PostgrestRestTemplate implements PostgrestClient {

    /**
     * restTemplate
     */
    private final RestTemplate restTemplate;

    private final String baseUrl;

    /**
     * Default RestTemplate constructor
     *
     * @param restTemplate restTemplate
     * @param baseUrl      baseUrl of postgrest
     * @return concrete client
     */
    public static PostgrestRestTemplate of(RestTemplate restTemplate, String baseUrl) {
        return new PostgrestRestTemplate(restTemplate, baseUrl);
    }

    @Override
    public <T> RangeResponse<T> search(String resource, Map<String, List<String>> params,
                                       Map<String, List<String>> headers, Class<T> clazz) {
        ResponseEntity<List<T>> response = restTemplate.exchange(
                getUri(resource, params), HttpMethod.GET,
                new HttpEntity<>(null, toHeaders(headers)), listRef(clazz));

        // Retrieve result headers
        return Optional.of(response)
                .map(HttpEntity::getBody)
                .map(x -> {
                    HeaderRange range = ResponseUtils.getCount(response.getHeaders())
                            .orElse(null);
                    return new RangeResponse<>(x, range);
                }).orElse(new RangeResponse<>(List.of(), null));
    }

    @Override
    public <T> BulkResponse<T> post(String resource, Map<String, List<String>> params, Object value, Map<String, List<String>> headers, Class<T> clazz) {
        ResponseEntity<List<T>> response = restTemplate.exchange(
                getUri(resource, params),
                HttpMethod.POST, new HttpEntity<>(value, toHeaders(headers)), listRef(clazz));
        return toBulkResponse(response);
    }

    @Override
    public <T> BulkResponse<T> patch(String resource, Map<String, List<String>> params, Object value, Map<String, List<String>> headers, Class<T> clazz) {
        ResponseEntity<List<T>> response = restTemplate.exchange(
                getUri(resource, params),
                HttpMethod.PATCH, new HttpEntity<>(value, toHeaders(headers)), listRef(clazz));
        return toBulkResponse(response);
    }

    @Override
    public <T> BulkResponse<T> delete(String resource, Map<String, List<String>> params, Map<String, List<String>> headers, Class<T> clazz) {
        ResponseEntity<List<T>> response = restTemplate.exchange(
                getUri(resource, params),
                HttpMethod.DELETE, new HttpEntity<>(null, toHeaders(headers)), listRef(clazz));
        return toBulkResponse(response);
    }

    @Override
    public List<CountItem> count(String resource, Map<String, List<String>> map) {
        return restTemplate.exchange(
                getUri(resource, map), HttpMethod.GET, new HttpEntity<>(null, new HttpHeaders()), listRef(CountItem.class)).getBody();
    }

    @Override
    public <V> V rpc(String rpcName, Map<String, List<String>> map, Object body, Type type) {
        try {
            return (V) restTemplate.exchange(
                    getUri(rpcName, map), HttpMethod.POST, new HttpEntity<>(body, new HttpHeaders()), ParameterizedTypeReference.forType(type))
                    .getBody();
        } catch (HttpClientErrorException.NotFound exception) {
            throw new PostgrestRequestException(rpcName, exception.getMessage(), exception, exception.getResponseBodyAsString());
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
     * Convert map to HttpHeaders
     *
     * @param headers map
     * @return HttpHeaders
     */
    private static HttpHeaders toHeaders(Map<String, List<String>> headers) {
        return new HttpHeaders(toMultiMap(headers));
    }

    /**
     * Get URI from resource and params
     *
     * @param resource resource
     * @param params   params
     * @return URI
     */
    private URI getUri(String resource, Map<String, List<String>> params) {
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(baseUrl).path(resource);
        if (Objects.nonNull(params)) {
            uriBuilder = uriBuilder.queryParams(toMultiMap(params));
        }
        return uriBuilder.build().encode().toUri();
    }

}
