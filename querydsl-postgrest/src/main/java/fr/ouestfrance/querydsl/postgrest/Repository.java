package fr.ouestfrance.querydsl.postgrest;

import fr.ouestfrance.querydsl.postgrest.model.BulkOptions;
import fr.ouestfrance.querydsl.postgrest.model.BulkResponse;
import fr.ouestfrance.querydsl.postgrest.model.Page;
import fr.ouestfrance.querydsl.postgrest.model.Pageable;
import fr.ouestfrance.querydsl.postgrest.model.exceptions.PostgrestRequestException;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface
 *
 * @param <T> type of return object
 */
public interface Repository<T> {

    /**
     * Search from criteria object
     *
     * @param criteria search criteria
     * @return page result
     */
    default Page<T> search(Object criteria) {
        return search(criteria, Pageable.unPaged());
    }


    /**
     * Count all items
     *
     * @return count result
     */
    default long count() {
        return count(null);
    }

    /**
     * Count from criteria object
     *
     * @param criteria search criteria
     * @return count result
     */
    long count(Object criteria);

    /**
     * Search from criteria object with pagination
     *
     * @param criteria search criteria
     * @param pageable pagination data
     * @return page result
     */
    Page<T> search(Object criteria, Pageable pageable);

    /**
     * Find one object using criteria, method can return one or empty
     *
     * @param criteria search criteria
     * @return Optional result
     * @throws PostgrestRequestException when search criteria result gave more than one item
     */
    Optional<T> findOne(Object criteria);

    /**
     * Get one object using criteria, method should return the response
     *
     * @param criteria search criteria
     * @return Result object
     * @throws PostgrestRequestException no element found, or more than one item
     */
    T getOne(Object criteria);

    /**
     * Post a value
     *
     * @param value to post
     * @return value inserted
     */
    default T post(Object value) {
        BulkResponse<T> post = post(List.of(value));
        if (post == null) {
            return null;
        }
        return post.stream().findFirst().orElse(null);
    }

    /**
     * Post multiple values
     *
     * @param values values to post
     * @return values inserted
     */
    default BulkResponse<T> post(List<Object> values) {
        return post(values, new BulkOptions());
    }

    /**
     * Post multiple values with bulkMode
     *
     * @param value   values to post
     * @param options bulk options
     * @return bulk response
     */
    BulkResponse<T> post(List<Object> value, BulkOptions options);

    /**
     * Upsert a value
     *
     * @param value to upsert
     * @return inserted or updated value
     */
    default T upsert(Object value) {
        BulkResponse<T> upsert = upsert(List.of(value));
        if (upsert == null) {
            return null;
        }
        return upsert.stream().findFirst().orElse(null);
    }

    /**
     * Upsert multiple values
     *
     * @param values values to upsert
     * @return values inserted or updated
     */
    default BulkResponse<T> upsert(List<Object> values) {
        return upsert(values, new BulkOptions());
    }

    /**
     * Upsert multiple values with bulkMode
     *
     * @param values  values to upsert
     * @param options bulk options
     * @return bulk response
     */
    BulkResponse<T> upsert(List<Object> values, BulkOptions options);

    /**
     * Update multiple body
     *
     * @param criteria criteria data
     * @param body     to update
     * @return list of patched object
     */
    default BulkResponse<T> patch(Object criteria, Object body) {
        return patch(criteria, body, new BulkOptions());
    }

    /**
     * Update multiple body
     *
     * @param criteria criteria data
     * @param body     to update
     * @param options  bulk options
     * @return list of patched object
     */
    BulkResponse<T> patch(Object criteria, Object body, BulkOptions options);


    /**
     * Delete items using criteria
     *
     * @param criteria criteria to create deletion query
     * @return list of deleted items
     */
    default BulkResponse<T> delete(Object criteria) {
        return delete(criteria, new BulkOptions());
    }

    /**
     * Delete items using criteria
     *
     * @param criteria criteria to create deletion query
     * @param options  bulk options
     * @return list of deleted items
     */
    BulkResponse<T> delete(Object criteria, BulkOptions options);


}
