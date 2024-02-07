package fr.ouestfrance.querydsl.postgrest;

import fr.ouestfrance.querydsl.postgrest.model.CountItem;
import fr.ouestfrance.querydsl.postgrest.model.Page;
import fr.ouestfrance.querydsl.postgrest.model.PageImpl;
import fr.ouestfrance.querydsl.postgrest.model.Range;
import fr.ouestfrance.querydsl.postgrest.model.RangeResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.reflect.TypeUtils;
import org.springframework.core.ParameterizedTypeReference;
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
import java.util.stream.Stream;

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
        return
                new PostgrestWebClient(webClient);
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

    private static void safeAdd(Map<String, List<String>> headers, HttpHeaders httpHeaders) {
        Optional.ofNullable(headers)
                .map(PostgrestWebClient::toMultiMap).ifPresent(httpHeaders::addAll);
        // Add contentType with default on call if webclient default is not set
        httpHeaders.put(CONTENT_TYPE, List.of(MediaType.APPLICATION_JSON_VALUE));
    }

    @Override
    public <T> List<T> post(String resource, List<Object> value, Map<String, List<String>> headers, Class<T> clazz) {
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
    public <T> List<T> patch(String resource, Map<String, List<String>> params, Object value, Map<String, List<String>> headers, Class<T> clazz) {
        return webClient.patch().uri(uriBuilder -> {
                    uriBuilder.path(resource);
                    uriBuilder.queryParams(toMultiMap(params));
                    return uriBuilder.build();
                })
                .bodyValue(value)
                .headers(httpHeaders -> safeAdd(headers, httpHeaders))
                .retrieve()
                .bodyToMono(listRef(clazz)).block();
    }

    @Override
    public <T> List<T> delete(String resource, Map<String, List<String>> params, Map<String, List<String>> headers, Class<T> clazz) {
        return webClient.delete().uri(uriBuilder -> {
                    uriBuilder.path(resource);
                    uriBuilder.queryParams(toMultiMap(params));
                    return uriBuilder.build();
                }).headers(httpHeaders -> safeAdd(headers, httpHeaders))
                .retrieve()
                .bodyToMono(listRef(clazz)).block();
    }

    private static <T> ParameterizedTypeReference<List<T>> listRef(Class<T> clazz) {
        return ParameterizedTypeReference.forType(TypeUtils.parameterize(List.class, clazz));
    }

    private static MultiValueMap<String, String> toMultiMap(Map<String, List<String>> params) {
        return new LinkedMultiValueMap<>(params);
    }

}
