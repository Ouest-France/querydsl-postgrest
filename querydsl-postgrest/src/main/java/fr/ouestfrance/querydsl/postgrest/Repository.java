package fr.ouestfrance.querydsl.postgrest;

import fr.ouestfrance.querydsl.postgrest.model.BulkOptions;
import fr.ouestfrance.querydsl.postgrest.model.BulkResponse;
import fr.ouestfrance.querydsl.postgrest.model.Page;
import fr.ouestfrance.querydsl.postgrest.model.Pageable;
import fr.ouestfrance.querydsl.postgrest.model.exceptions.PostgrestRequestException;

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
     * Upsert a value
     *
     * @param value to upsert
     * @return upsert value
     */
    default T upsert(Object value) {
        List<T> upsert = upsert(List.of(value));
        return upsert.stream().findFirst().orElse(null);
    }

    /**
     * Upsert multiple values
     *
     * @param value values to upsert
     * @return values inserted or updated
     */
    default BulkResponse<T> upsert(List<Object> value) {
        return upsert(value, new BulkOptions());
    }

    BulkResponse<T> upsert(List<Object> value, BulkOptions options);


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

    BulkResponse<T> delete(Object criteria, BulkOptions options);

}
