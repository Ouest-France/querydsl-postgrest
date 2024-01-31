package fr.ouestfrance.querydsl.postgrest.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * BulkRequest
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class BulkRequest {
    /**
     * Default headers
     */
    Map<String, List<String>> headers;
}
