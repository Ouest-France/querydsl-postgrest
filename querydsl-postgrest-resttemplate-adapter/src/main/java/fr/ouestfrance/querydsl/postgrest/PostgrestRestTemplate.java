package fr.ouestfrance.querydsl.postgrest;

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
import java.util.stream.Stream;

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
                    Range range = Optional.ofNullable(response.getHeaders().get("Content-Range"))
                            .map(List::stream)
                            .map(Stream::findFirst)
                            .filter(Optional::isPresent)
                            .map(Optional::get)
                            .map(Range::of).orElse(null);
                    return new RangeResponse<>(x, range);
                }).orElse(new RangeResponse<>(List.of(), null));
    }

    @Override
    public <T> List<T> post(String resource, List<Object> value, Map<String, List<String>> headers, Class<T> clazz) {
        HttpHeaders httpHeaders = toHeaders(headers);
        return restTemplate.exchange(resource, HttpMethod.POST, new HttpEntity<>(value, httpHeaders), listRef(clazz)).getBody();
    }

    @Override
    public <T> List<T> patch(String resource, Map<String, List<String>> params, Object value, Map<String, List<String>> headers, Class<T> clazz) {
        MultiValueMap<String, String> queryParams = toMultiMap(params);
        return restTemplate.exchange(restTemplate.getUriTemplateHandler()
                                .expand(UriComponentsBuilder.fromPath(resource).queryParams(queryParams).build().toString(), new HashMap<>()),
                        HttpMethod.PATCH, new HttpEntity<>(value, toHeaders(headers)), listRef(clazz))
                .getBody();
    }

    @Override
    public <T> List<T> delete(String resource, Map<String, List<String>> params, Map<String, List<String>> headers, Class<T> clazz) {
        MultiValueMap<String, String> queryParams = toMultiMap(params);
        return restTemplate.exchange(restTemplate.getUriTemplateHandler().expand(UriComponentsBuilder.fromPath(resource)
                .queryParams(queryParams).build().toString(), new HashMap<>()), HttpMethod.DELETE, new HttpEntity<>(null, toHeaders(headers)), listRef(clazz)).getBody();
    }

    private static <T> ParameterizedTypeReference<List<T>> listRef(Class<T> clazz) {
        return ParameterizedTypeReference.forType(TypeUtils.parameterize(List.class, clazz));
    }

    private static MultiValueMap<String, String> toMultiMap(Map<String, List<String>> params) {
        return new LinkedMultiValueMap<>(params);
    }

    private static HttpHeaders toHeaders(Map<String, List<String>> headers) {
        return new HttpHeaders(toMultiMap(headers));
    }
}
