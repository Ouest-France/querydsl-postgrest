package fr.ouestfrance.querydsl.postgrest;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

/**
 * Rest interface for querying postgrest
 */
public class PostgrestClient {

    private final WebClient webClient;

    /**
     * Default constructor for PostgrestClient
     *
     * @param webClient webClient
     */
    public PostgrestClient(WebClient webClient) {
        this.webClient = webClient;
    }

    /**
     * Search method
     *
     * @param resource resource name
     * @param params   query params
     * @param <T>      return type
     * @param headers  header params
     * @return ResponseEntity containing the results
     */

    public <T> ResponseEntity<List<T>> search(String resource, MultiValueMap<String, String> params,
                                              MultiValueMap<String, String> headers) {
        return webClient.get().uri(uriBuilder -> {
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
    }

    /**
     * Save body
     *
     * @param resource resource name
     * @param value    data to save
     * @param headers  headers to pass
     * @param <T>      return type
     * @return list of inserted objects
     */
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

    /**
     * Patch data
     *
     * @param resource resource name
     * @param params   criteria to apply on patch
     * @param value    object to patch
     * @param headers  headers to pass
     * @param <T>      return type
     * @return list of patched objects
     */
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

    /**
     * Delete data
     *
     * @param resource resource name
     * @param params   query params
     * @param headers  headers to pass
     * @param <T>      return type
     * @return list of deleted objects
     */
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
