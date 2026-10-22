package fr.ouestfrance.querydsl.postgrest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PatchExchange;
import org.springframework.web.service.annotation.PostExchange;

import java.net.URI;
import java.util.List;
import java.util.Map;

/**
 * Declarative http interface used to reach postgrest.
 * Every method returns the raw body, the json translation is done by {@link JsonResponseReader}.
 */
public interface PostgrestExchange {

    /**
     * GET request
     *
     * @param uri     full uri (with query string)
     * @param headers request headers
     * @return raw response
     */
    @HttpExchange(method = "GET", accept = "application/json", contentType = "application/json")
    ResponseEntity<byte[]> get(URI uri, @RequestHeader Map<String, List<String>> headers);

    /**
     * POST request
     *
     * @param uri     full uri (with query string)
     * @param headers request headers
     * @param body    body to serialize (optional)
     * @return raw response
     */
    @PostExchange(accept = "application/json", contentType = "application/json")
    ResponseEntity<byte[]> post(URI uri, @RequestHeader Map<String, List<String>> headers,
                                @RequestBody(required = false) Object body);

    /**
     * PATCH request
     *
     * @param uri     full uri (with query string)
     * @param headers request headers
     * @param body    body to serialize
     * @return raw response
     */
    @PatchExchange(accept = "application/json", contentType = "application/json")
    ResponseEntity<byte[]> patch(URI uri, @RequestHeader Map<String, List<String>> headers,
                                 @RequestBody(required = false) Object body);

    /**
     * DELETE request
     *
     * @param uri     full uri (with query string)
     * @param headers request headers
     * @return raw response
     */
    @HttpExchange(method = "DELETE", accept = "application/json", contentType = "application/json")
    ResponseEntity<byte[]> delete(URI uri, @RequestHeader Map<String, List<String>> headers);

    /**
     * HEAD request
     *
     * @param uri     full uri (with query string)
     * @param headers request headers
     * @return raw response (without body)
     */
    @HttpExchange(method = "HEAD", contentType = "application/json")
    ResponseEntity<byte[]> head(URI uri, @RequestHeader Map<String, List<String>> headers);
}
