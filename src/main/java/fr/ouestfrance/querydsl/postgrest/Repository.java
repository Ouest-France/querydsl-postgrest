package fr.ouestfrance.querydsl.postgrest;

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
     * Resource name of the repository
     *
     * @return resourceName of the repository
     */
    String resourceName();

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
    default Optional<T> findOne(Object criteria) {
        Page<T> search = search(criteria);
        if (search.getTotalElements() > 1) {
            throw new PostgrestRequestException(resourceName(),
                    "Search with params " + criteria + " must found only one result, but found " + search.getTotalElements() + " results");
        }
        return search.stream().findFirst();
    }

    /**
     * Get one object using criteria, method should return the response
     *
     * @param criteria search criteria
     * @return Result object
     * @throws PostgrestRequestException no element found, or more than one item
     */
    default T getOne(Object criteria) {
        return findOne(criteria).orElseThrow(
                () -> new PostgrestRequestException(resourceName(),
                        "Search with params " + criteria + " must return one result, but found 0"));
    }


    /**
     * Upsert a value
     *
     * @param value to upsert
     * @return upsert value
     */
    default T upsert(Object value) {
        return upsert(List.of(value)).stream().findFirst().orElse(null);
    }

    /**
     * Upsert multiple values
     *
     * @param value values to upsert
     * @return values inserted or updated
     */
    List<T> upsert(List<Object> value);

    /**
     * Update multiple body
     *
     * @param criteria criteria data
     * @param body     to update
     * @return list of patched object
     */
    List<T> patch(Object criteria, Object body);


    /**
     * Delete items using criteria
     *
     * @param criteria criteria to create deletion query
     * @return list of deleted items
     */
    List<T> delete(Object criteria);

}
