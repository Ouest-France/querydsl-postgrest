package fr.ouestfrance.querydsl.postgrest.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Bulk response allow to retrieve the number of affected rows and the data
 *
 * @param <T> type of data
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class BulkResponse<T> extends ArrayList<T> {

    public BulkResponse(List<T> data, long count) {
        super(data != null ? data : new ArrayList<>());
        this.count = count;
    }

    /**
     * Number of affected rows
     */
    public final long count;

    /**
     * Create a bulk response from a list of items
     *
     * @param items items to add
     * @param <T>   type of data
     * @return bulk response
     */
    public static <T> BulkResponse<T> of(T... items) {
        return new BulkResponse<>(new ArrayList<>(List.of(items)), items.length);
    }

}
