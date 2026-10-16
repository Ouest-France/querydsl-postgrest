package fr.ouestfrance.querydsl.postgrest;

import fr.ouestfrance.querydsl.postgrest.model.BulkResponse;
import fr.ouestfrance.querydsl.postgrest.model.HeaderRange;
import fr.ouestfrance.querydsl.postgrest.model.RangeResponse;
import fr.ouestfrance.querydsl.postgrest.model.exceptions.PostgrestRequestException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ClassUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;
import org.springframework.web.util.UriComponentsBuilder;
import tools.jackson.databind.json.JsonMapper;

import java.lang.reflect.Type;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static fr.ouestfrance.querydsl.postgrest.ResponseUtils.toBulkResponse;

/**
 * PostgrestClient built on a {@link PostgrestExchange} http interface.
 * The caller chooses the http client (RestClient, WebClient, ...) by creating the {@link HttpServiceProxyFactory}.
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class PostgrestHttpExchange implements PostgrestClient {

    private static final boolean WEBCLIENT_PRESENT = ClassUtils.isPresent(
            "org.springframework.web.reactive.function.client.WebClientResponseException",
            PostgrestHttpExchange.class.getClassLoader());

    private final PostgrestExchange exchange;

    private final String baseUrl;

    private final JsonResponseReader reader;

    /**
     * Build a client from an http interface
     *
     * @param exchange http interface
     * @param baseUrl  baseUrl of postgrest
     * @return concrete client
     */
    public static PostgrestHttpExchange of(PostgrestExchange exchange, String baseUrl) {
        return new PostgrestHttpExchange(exchange, baseUrl, new JsonResponseReader());
    }

    /**
     * Build a client from a proxy factory
     *
     * @param factory proxy factory (RestClientAdapter, WebClientAdapter, ...)
     * @param baseUrl baseUrl of postgrest
     * @return concrete client
     */
    public static PostgrestHttpExchange of(HttpServiceProxyFactory factory, String baseUrl) {
        return of(factory.createClient(PostgrestExchange.class), baseUrl);
    }

    /**
     * Build a client from a proxy factory with a specific json mapper for responses
     *
     * @param factory    proxy factory (RestClientAdapter, WebClientAdapter, ...)
     * @param baseUrl    baseUrl of postgrest
     * @param jsonMapper mapper used to read responses
     * @return concrete client
     */
    public static PostgrestHttpExchange of(HttpServiceProxyFactory factory, String baseUrl, JsonMapper jsonMapper) {
        return new PostgrestHttpExchange(factory.createClient(PostgrestExchange.class), baseUrl,
                new JsonResponseReader(jsonMapper));
    }

    @Override
    public <T> RangeResponse<T> search(String resource, Map<String, List<String>> params,
                                       Map<String, List<String>> headers, Class<T> clazz) {
        ResponseEntity<byte[]> response = exchange.get(getUri(resource, params), nonNull(headers));
        HeaderRange range = ResponseUtils.getCount(response.getHeaders()).orElse(null);
        return new RangeResponse<>(reader.readList(response.getBody(), clazz), range);
    }

    @Override
    public <T> BulkResponse<T> post(String resource, Map<String, List<String>> params, Object value,
                                    Map<String, List<String>> headers, Class<T> clazz) {
        return toBulk(exchange.post(getUri(resource, params), nonNull(headers), value), clazz);
    }

    @Override
    public <T> BulkResponse<T> patch(String resource, Map<String, List<String>> params, Object value,
                                     Map<String, List<String>> headers, Class<T> clazz) {
        return toBulk(exchange.patch(getUri(resource, params), nonNull(headers), value), clazz);
    }

    @Override
    public <T> BulkResponse<T> delete(String resource, Map<String, List<String>> params,
                                      Map<String, List<String>> headers, Class<T> clazz) {
        return toBulk(exchange.delete(getUri(resource, params), nonNull(headers)), clazz);
    }

    @Override
    public long count(String resource, Map<String, List<String>> map) {
        Map<String, List<String>> headers = Map.of("Range-Unit", List.of("items"), "Prefer", List.of("count=exact"));
        ResponseEntity<byte[]> response = exchange.head(getUri(resource, map), headers);
        return ResponseUtils.getCount(response.getHeaders())
                .map(HeaderRange::getTotalElements)
                .orElse(-1L);
    }

    @Override
    public <V> V rpc(String rpcName, Map<String, List<String>> map, Object body, Type type) {
        try {
            ResponseEntity<byte[]> response = exchange.post(getUri(rpcName, map), Map.of(), body);
            return reader.read(response.getBody(), type);
        } catch (RuntimeException exception) {
            if (isNotFound(exception)) {
                throw new PostgrestRequestException(rpcName, exception.getMessage(), exception, responseBody(exception));
            }
            throw exception;
        }
    }

    private <T> BulkResponse<T> toBulk(ResponseEntity<byte[]> response, Class<T> clazz) {
        return toBulkResponse(reader.readList(response.getBody(), clazz), response.getHeaders());
    }

    private static Map<String, List<String>> nonNull(Map<String, List<String>> headers) {
        return headers == null ? Map.of() : headers;
    }

    private static boolean isNotFound(RuntimeException exception) {
        if (exception instanceof HttpStatusCodeException statusException) {
            return statusException.getStatusCode().isSameCodeAs(HttpStatusCode.valueOf(404));
        }
        return WEBCLIENT_PRESENT && WebClientErrors.isNotFound(exception);
    }

    private static String responseBody(RuntimeException exception) {
        if (exception instanceof HttpStatusCodeException statusException) {
            return statusException.getResponseBodyAsString();
        }
        return WEBCLIENT_PRESENT ? WebClientErrors.responseBody(exception) : null;
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
