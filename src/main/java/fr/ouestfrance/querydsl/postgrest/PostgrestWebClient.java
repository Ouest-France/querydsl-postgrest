package fr.ouestfrance.querydsl.postgrest;

import fr.ouestfrance.querydsl.postgrest.model.Page;
import fr.ouestfrance.querydsl.postgrest.model.PageImpl;
import fr.ouestfrance.querydsl.postgrest.model.Range;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.reflect.TypeUtils;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

/**
 * Rest interface for querying postgrest
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class PostgrestWebClient implements PostgrestClient {

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
    public <T> Page<T> search(String resource, MultiValueMap<String, String> params,
                              MultiValueMap<String, String> headers, Class<T> clazz) {
        ResponseEntity<List<T>> response = webClient.get().uri(uriBuilder -> {
                    uriBuilder.path(resource);
                    uriBuilder.queryParams(params);
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
                    PageImpl<T> page = new PageImpl<>(x, null, x.size(), 1);
                    List<String> contentRangeHeaders = response.getHeaders().get("Content-Range");
                    if (contentRangeHeaders != null && !contentRangeHeaders.isEmpty()) {
                        Range range = Range.of(contentRangeHeaders.stream().findFirst().toString());
                        page.withRange(range);
                    }
                    return (Page<T>) page;
                }).orElse(Page.empty());
    }

    private static void safeAdd(MultiValueMap<String, String> headers, HttpHeaders httpHeaders) {
        Optional.ofNullable(headers).ifPresent(httpHeaders::addAll);
    }

    @Override
    public <T> List<T> post(String resource, List<Object> value, MultiValueMap<String, String> headers, Class<T> clazz) {
        return webClient.post().uri(uriBuilder -> {
                    uriBuilder.path(resource);
                    return uriBuilder.build();
                }).headers(httpHeaders -> safeAdd(headers, httpHeaders))
                .bodyValue(value)
                .retrieve()
                .bodyToMono(listRef(clazz))
                .block();
    }


    @Override
    public <T> List<T> patch(String resource, MultiValueMap<String, String> params, Object value, MultiValueMap<String, String> headers, Class<T> clazz) {
        return webClient.patch().uri(uriBuilder -> {
                    uriBuilder.path(resource);
                    uriBuilder.queryParams(params);
                    return uriBuilder.build();
                })
                .bodyValue(value)
                .headers(httpHeaders -> safeAdd(headers, httpHeaders))
                .retrieve()
                .bodyToMono(listRef(clazz)).block();
    }

    @Override
    public <T> List<T> delete(String resource, MultiValueMap<String, String> params, MultiValueMap<String, String> headers, Class<T> clazz) {
        return webClient.delete().uri(uriBuilder -> {
                    uriBuilder.path(resource);
                    uriBuilder.queryParams(params);
                    return uriBuilder.build();
                }).headers(httpHeaders -> safeAdd(headers, httpHeaders))
                .retrieve()
                .bodyToMono(listRef(clazz)).block();
    }

    private static <T> ParameterizedTypeReference<List<T>> listRef(Class<T> clazz) {
        return ParameterizedTypeReference.forType(TypeUtils.parameterize(List.class, clazz));
    }


}
