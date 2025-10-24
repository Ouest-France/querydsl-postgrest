package fr.ouestfrance.querydsl.postgrest;

import fr.ouestfrance.querydsl.postgrest.model.BulkResponse;
import fr.ouestfrance.querydsl.postgrest.model.RangeResponse;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

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

    <T> RangeResponse<T> search(String resource, Map<String, List<String>> params,
                                Map<String, List<String>> headers, Class<T> clazz);

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
    <T> BulkResponse<T> post(String resource, Map<String, List<String>> queryParams, Object value, Map<String, List<String>> headers, Class<T> clazz);

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
    <T> BulkResponse<T> patch(String resource, Map<String, List<String>> params, Object value, Map<String, List<String>> headers, Class<T> clazz);

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
    <T> BulkResponse<T> delete(String resource, Map<String, List<String>> params, Map<String, List<String>> headers, Class<T> clazz);

    /**
     * Count data
     *
     * @param resource resource name
     * @param map      query params
     * @return the total number of elements
     */
    long count(String resource, Map<String, List<String>> map);

    /**
     * Call RPC
     *
     * @param rpcName rpc name
     * @param params  query params
     * @param body    body request to send
     * @param type    class of return
     * @param <V>     type of return object
     * @return MultiValueMap
     */
    <V> V rpc(String rpcName, Map<String, List<String>> params, Object body, Type type);
}
