package fr.ouestfrance.querydsl.postgrest;

import fr.ouestfrance.querydsl.postgrest.model.Page;
import fr.ouestfrance.querydsl.postgrest.model.PageImpl;
import fr.ouestfrance.querydsl.postgrest.model.Range;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.reflect.TypeUtils;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.List;
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
    public <T> Page<T> search(String resource, MultiValueMap<String, String> params,
                              MultiValueMap<String, String> headers, Class<T> clazz) {
        ResponseEntity<List<T>> response = restTemplate.exchange(restTemplate.getUriTemplateHandler().expand(UriComponentsBuilder.fromPath(resource).queryParams(params).build().toString(), new HashMap<>()), HttpMethod.GET, new HttpEntity<>(null, headers), listRef(clazz));
        // Retrieve result headers
        return Optional.ofNullable(response)
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
    public <T> List<T> post(String resource, List<Object> value, MultiValueMap<String, String> headers, Class<T> clazz) {
        return restTemplate.exchange(resource, HttpMethod.POST, new HttpEntity<>(value, headers), listRef(clazz)).getBody();
    }


    @Override
    public <T> List<T> patch(String resource, MultiValueMap<String, String> params, Object value, MultiValueMap<String, String> headers, Class<T> clazz) {
        return restTemplate.exchange(restTemplate.getUriTemplateHandler().expand(UriComponentsBuilder.fromPath(resource).queryParams(params).build().toString(), new HashMap<>()), HttpMethod.PATCH, new HttpEntity<>(value, headers), listRef(clazz)).getBody();
    }

    @Override
    public <T> List<T> delete(String resource, MultiValueMap<String, String> params, MultiValueMap<String, String> headers, Class<T> clazz) {
        return restTemplate.exchange(restTemplate.getUriTemplateHandler().expand(UriComponentsBuilder.fromPath(resource).queryParams(params).build().toString(), new HashMap<>()), HttpMethod.DELETE, new HttpEntity<>(null, headers), listRef(clazz)).getBody();
    }

    private static <T> ParameterizedTypeReference<List<T>> listRef(Class<T> clazz) {
        return ParameterizedTypeReference.forType(TypeUtils.parameterize(List.class, clazz));
    }


}
