package fr.ouestfrance.querydsl.postgrest;

import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.*;

import java.util.List;

/**
 * Rest interface for querying postgrest
 */
public interface PostgrestClient {
    /**
     * Search method
     *
     * @param resource resource name
     * @param params   query params
     * @param headers  header params
     * @return ResponseEntity containing the results
     */
    @GetExchange("/{resource}")
    ResponseEntity<List<Object>> search(@PathVariable("resource") String resource, @RequestParam MultiValueMap<String, Object> params,
                                        @RequestHeader MultiValueMap<String, Object> headers);

    /**
     * Save body
     *
     * @param resource resource name
     * @param value    data to save
     * @param headers  headers to pass
     * @return list of inserted objects
     */
    @PostExchange("/{resource}")
    List<Object> post(@PathVariable("resource") String resource, @RequestBody List<Object> value, @RequestHeader MultiValueMap<String, Object> headers);

    /**
     * Patch data
     *
     * @param resource resource name
     * @param params   criteria to apply on patch
     * @param value    object to patch
     * @param headers  headers to pass
     * @return list of patched objects
     */
    @PatchExchange("/{resource}")
    List<Object> patch(@PathVariable("resource") String resource, @RequestParam MultiValueMap<String, Object> params, @RequestBody Object value, @RequestHeader MultiValueMap<String, Object> headers);

    /**
     * Delete data
     *
     * @param resource resource name
     * @param params   query params
     * @param headers  headers to pass
     * @return list of deleted objects
     */
    @DeleteExchange("/{resource}")
    List<Object> delete(@PathVariable("resource") String resource, @RequestParam MultiValueMap<String, Object> params, @RequestHeader MultiValueMap<String, Object> headers);

}
