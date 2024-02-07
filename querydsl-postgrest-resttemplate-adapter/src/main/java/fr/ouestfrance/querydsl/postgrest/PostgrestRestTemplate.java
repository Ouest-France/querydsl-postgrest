package fr.ouestfrance.querydsl.postgrest;

import fr.ouestfrance.querydsl.postgrest.model.BulkResponse;
import fr.ouestfrance.querydsl.postgrest.model.CountItem;
import fr.ouestfrance.querydsl.postgrest.model.Range;
import fr.ouestfrance.querydsl.postgrest.model.RangeResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.reflect.TypeUtils;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    /**
     * Postgrest restTemplate adapter
     *
     * @param restTemplate restTemplate
     * @return PostgrestRestTemplate implementation
     */
    public static PostgrestRestTemplate of(RestTemplate restTemplate) {
        return new PostgrestRestTemplate(restTemplate);
    }

    @Override
    public <T> RangeResponse<T> search(String resource, Map<String, List<String>> params,
                                       Map<String, List<String>> headers, Class<T> clazz) {
        ResponseEntity<List<T>> response = restTemplate.exchange(restTemplate.getUriTemplateHandler()
                        .expand(UriComponentsBuilder.fromPath(resource)
                                .queryParams(toMultiMap(params)).build().toString(), new HashMap<>()), HttpMethod.GET,
                new HttpEntity<>(null, toHeaders(headers)), listRef(clazz));
        // Retrieve result headers
        return Optional.of(response)
                .map(HttpEntity::getBody)
                .map(x -> {
                    Range range = ResponseUtils.getCount(response.getHeaders())
                            .orElse(null);
                    return new RangeResponse<>(x, range);
                }).orElse(new RangeResponse<>(List.of(), null));
    }

    @Override
    public <T> BulkResponse<T> post(String resource, List<Object> value, Map<String, List<String>> headers, Class<T> clazz) {
        ResponseEntity<List<T>> response = restTemplate.exchange(resource, HttpMethod.POST, new HttpEntity<>(value, toHeaders(headers)), listRef(clazz));
        return toBulkResponse(response);
    }


    @Override
    public <T> BulkResponse<T> patch(String resource, Map<String, List<String>> params, Object value, Map<String, List<String>> headers, Class<T> clazz) {
        ResponseEntity<List<T>> response = restTemplate.exchange(restTemplate.getUriTemplateHandler()
                        .expand(UriComponentsBuilder.fromPath(resource).queryParams(toMultiMap(params)).build().toString(), new HashMap<>()),
                HttpMethod.PATCH, new HttpEntity<>(value, toHeaders(headers)), listRef(clazz));
        return toBulkResponse(response);
    }

    @Override
    public <T> BulkResponse<T> delete(String resource, Map<String, List<String>> params, Map<String, List<String>> headers, Class<T> clazz) {
        ResponseEntity<List<T>> response = restTemplate.exchange(restTemplate.getUriTemplateHandler().expand(UriComponentsBuilder.fromPath(resource)
                .queryParams(toMultiMap(params)).build().toString(), new HashMap<>()), HttpMethod.DELETE, new HttpEntity<>(null, toHeaders(headers)), listRef(clazz));
        return toBulkResponse(response);
    }

    @Override
    public List<CountItem> count(String resource, Map<String, List<String>> map) {
        return restTemplate.exchange(restTemplate.getUriTemplateHandler().expand(UriComponentsBuilder.fromPath(resource)
                .queryParams(toMultiMap(map)).build().toString(), new HashMap<>()), HttpMethod.GET, new HttpEntity<>(null, new HttpHeaders()), listRef(CountItem.class)).getBody();
    }


    private static MultiValueMap<String, String> toMultiMap(Map<String, List<String>> params) {
        return new LinkedMultiValueMap<>(params);
    }

    private static HttpHeaders toHeaders(Map<String, List<String>> headers) {
        return new HttpHeaders(toMultiMap(headers));
    }

}
