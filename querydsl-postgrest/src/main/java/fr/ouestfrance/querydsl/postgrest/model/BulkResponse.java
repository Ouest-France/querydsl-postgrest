package fr.ouestfrance.querydsl.postgrest.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

/**
 * Bulk response allow to retrieve the number of affected rows and the data
 *
 * @param <T> type of data
 */
@Getter
@SuppressWarnings("java:S2160")
public class BulkResponse<T> extends ArrayList<T> {

    public BulkResponse(List<T> data, long affectedRows, long totalElements) {
        super(data != null ? data : new ArrayList<>());
        this.affectedRows = affectedRows;
        this.totalElements = totalElements;
    }

    /**
     * Number of affected rows
     */
    private long affectedRows;

    /**
     * Total elements in the criteria
     */
    private long totalElements;

    /**
     * Create a bulk response from a list of items
     *
     * @param items items to add
     * @param <T>   type of data
     * @return bulk response
     */
    public static <T> BulkResponse<T> of(T... items) {
        return new BulkResponse<>(new ArrayList<>(List.of(items)), items.length, items.length);
    }

    /**
     * Allow bulkResponse to be merged with another
     *
     * @param response response
     */
    public void merge(BulkResponse<T> response) {
        affectedRows += response.getAffectedRows();
        totalElements = response.getTotalElements();
        addAll(response);
    }
}
