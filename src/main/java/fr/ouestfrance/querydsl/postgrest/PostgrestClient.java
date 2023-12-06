package fr.ouestfrance.querydsl.postgrest;

import fr.ouestfrance.querydsl.postgrest.model.Page;
import org.springframework.util.MultiValueMap;

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
     * @param <T>      return type
     * @param headers  header params
     * @param clazz    type of return
     * @return ResponseEntity containing the results
     */

    <T> Page<T> search(String resource, MultiValueMap<String, String> params,
                       MultiValueMap<String, String> headers, Class<T> clazz);

    /**
     * Save body
     *
     * @param resource resource name
     * @param value    data to save
     * @param headers  headers to pass
     * @param <T>      return type
     * @param clazz    type of return
     * @return list of inserted objects
     */
    <T> List<T> post(String resource, List<Object> value, MultiValueMap<String, String> headers, Class<T> clazz);

    /**
     * Patch data
     *
     * @param resource resource name
     * @param params   criteria to apply on patch
     * @param value    object to patch
     * @param headers  headers to pass
     * @param <T>      return type
     * @param clazz    type of return
     * @return list of patched objects
     */
    <T> List<T> patch(String resource, MultiValueMap<String, String> params, Object value, MultiValueMap<String, String> headers, Class<T> clazz);

    /**
     * Delete data
     *
     * @param resource resource name
     * @param params   query params
     * @param headers  headers to pass
     * @param <T>      return type
     * @param clazz    type of return
     * @return list of deleted objects
     */
    <T> List<T> delete(String resource, MultiValueMap<String, String> params, MultiValueMap<String, String> headers, Class<T> clazz);

}
