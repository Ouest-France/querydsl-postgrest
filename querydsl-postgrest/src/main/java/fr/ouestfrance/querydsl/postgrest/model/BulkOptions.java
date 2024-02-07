package fr.ouestfrance.querydsl.postgrest.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * BulkOptions
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class BulkOptions {
    /**
     * Count only result
     */
    private boolean countOnly = false;
    /**
     * Allow to make multiple calls
     */
    private int pageSize = -1;
}
