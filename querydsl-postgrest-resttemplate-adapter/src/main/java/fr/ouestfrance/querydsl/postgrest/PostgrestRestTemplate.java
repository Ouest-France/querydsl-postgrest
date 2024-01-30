package fr.ouestfrance.querydsl.postgrest;

import fr.ouestfrance.querydsl.postgrest.model.BulkResponse;
import fr.ouestfrance.querydsl.postgrest.model.Page;
import fr.ouestfrance.querydsl.postgrest.model.PageImpl;
import fr.ouestfrance.querydsl.postgrest.model.Range;
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
     * @return PostgrestResttemplate implementation
     */
    public static PostgrestRestTemplate of(RestTemplate restTemplate) {
        return new PostgrestRestTemplate(restTemplate);
    }

    @Override
    public <T> Page<T> search(String resource, Map<String, List<String>> params,
                              Map<String, List<String>> headers, Class<T> clazz) {
        ResponseEntity<List<T>> response = restTemplate.exchange(restTemplate.getUriTemplateHandler()
                        .expand(UriComponentsBuilder.fromPath(resource)
                                .queryParams(toMultiMap(params)).build().toString(), new HashMap<>()), HttpMethod.GET,
                new HttpEntity<>(null, toHeaders(headers)), listRef(clazz));
        // Retrieve result headers
        return Optional.of(response)
                .map(HttpEntity::getBody)
                .map(x -> {
                    PageImpl<T> page = new PageImpl<>(x, null, x.size(), 1);
                    List<String> contentRangeHeaders = response.getHeaders().get("Content-Range");
                    if (contentRangeHeaders != null) {
                        Range range = Range.of(contentRangeHeaders.stream().findFirst().toString());
                        page.withRange(range);
                    }
                    return (Page<T>) page;
                }).orElse(Page.empty());
    }

    @Override
    public <T> BulkResponse<T> post(String resource, List<Object> value, Map<String, List<String>> headers, Class<T> clazz) {
        HttpHeaders httpHeaders = toHeaders(headers);
        ResponseEntity<List<T>> response = restTemplate.exchange(resource, HttpMethod.POST, new HttpEntity<>(value, httpHeaders), listRef(clazz));
        return new BulkResponse<>(response.getBody(), getCount(response.getHeaders()).map(Range::getCount).orElse(0L));
    }


    @Override
    public <T> BulkResponse<T> patch(String resource, Map<String, List<String>> params, Object value, Map<String, List<String>> headers, Class<T> clazz) {
        MultiValueMap<String, String> queryParams = toMultiMap(params);
        ResponseEntity<List<T>> response = restTemplate.exchange(restTemplate.getUriTemplateHandler()
                        .expand(UriComponentsBuilder.fromPath(resource).queryParams(queryParams).build().toString(), new HashMap<>()),
                HttpMethod.PATCH, new HttpEntity<>(value, toHeaders(headers)), listRef(clazz));
        return new BulkResponse<>(response.getBody(), getCount(response.getHeaders()).map(Range::getCount).orElse(0L));
    }

    @Override
    public <T> BulkResponse<T> delete(String resource, Map<String, List<String>> params, Map<String, List<String>> headers, Class<T> clazz) {
        MultiValueMap<String, String> queryParams = toMultiMap(params);
        ResponseEntity<List<T>> response = restTemplate.exchange(restTemplate.getUriTemplateHandler().expand(UriComponentsBuilder.fromPath(resource)
                .queryParams(queryParams).build().toString(), new HashMap<>()), HttpMethod.DELETE, new HttpEntity<>(null, toHeaders(headers)), listRef(clazz));
        return new BulkResponse<>(response.getBody(), getCount(response.getHeaders()).map(Range::getCount).orElse(0L));
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


    private static Optional<Range> getCount(HttpHeaders response) {
        return Optional.ofNullable(response.get("Content-Range"))
                .flatMap(x -> x.stream().findFirst())
                .map(Range::of);
    }
}
