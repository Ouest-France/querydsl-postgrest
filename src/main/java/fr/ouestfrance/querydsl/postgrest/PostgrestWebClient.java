package fr.ouestfrance.querydsl.postgrest;

import fr.ouestfrance.querydsl.postgrest.model.Page;
import fr.ouestfrance.querydsl.postgrest.model.PageImpl;
import fr.ouestfrance.querydsl.postgrest.model.Range;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;

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
                              MultiValueMap<String, String> headers) {
        ResponseEntity<List<T>> response = webClient.get().uri(uriBuilder -> {
                    uriBuilder.path(resource);
                    uriBuilder.queryParams(params);
                    return uriBuilder.build();
                }).headers(httpHeaders ->
                        httpHeaders.addAll(headers)
                )
                .retrieve()
                .toEntity(new ParameterizedTypeReference<List<T>>() {
                })
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

    @Override
    public <T> List<T> post(String resource, List<Object> value, MultiValueMap<String, String> headers) {
        return webClient.post().uri(uriBuilder -> {
                    uriBuilder.path(resource);
                    return uriBuilder.build();
                }).headers(httpHeaders -> httpHeaders.addAll(headers))
                .bodyValue(value)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<T>>() {
                }).block();
    }

    @Override
    public <T> List<T> patch(String resource, MultiValueMap<String, String> params, Object value, MultiValueMap<String, String> headers) {
        return webClient.patch().uri(uriBuilder -> {
                    uriBuilder.path(resource);
                    uriBuilder.queryParams(params);
                    return uriBuilder.build();
                })
                .bodyValue(value)
                .headers(httpHeaders -> httpHeaders.addAll(headers))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<T>>() {
                }).block();
    }

    @Override
    public <T> List<T> delete(String resource, MultiValueMap<String, String> params, MultiValueMap<String, String> headers) {
        return webClient.delete().uri(uriBuilder -> {
                    uriBuilder.path(resource);
                    uriBuilder.queryParams(params);
                    return uriBuilder.build();
                }).headers(httpHeaders -> httpHeaders.addAll(headers))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<T>>() {
                }).block();
    }

}
