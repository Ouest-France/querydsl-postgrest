package fr.ouestfrance.querydsl.postgrest;

import fr.ouestfrance.querydsl.postgrest.model.BulkResponse;
import fr.ouestfrance.querydsl.postgrest.model.HeaderRange;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

/**
 * Utility class that helps to transform response
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ResponseUtils {

    /**
     * Transform a ResponseEntity of list to bulkResponse
     *
     * @param response response entity
     * @param <T>      Type of response
     * @return BulkResponse
     */
    public static <T> BulkResponse<T> toBulkResponse(ResponseEntity<List<T>> response) {
        return Optional.ofNullable(response)
                .map(x -> {
                    Optional<HeaderRange> count = getCount(x.getHeaders());
                    return new BulkResponse<>(x.getBody(), count.map(HeaderRange::getCount).orElse(0L), count.map(HeaderRange::getTotalElements).orElse(0L));
                })
                .orElse(new BulkResponse<>(List.of(), 0L, 0L));
    }

    /**
     * Extract Range headers
     *
     * @param headers headers where Content-Range is
     * @return range object
     */
    public static Optional<HeaderRange> getCount(HttpHeaders headers) {
        return Optional.ofNullable(headers.get("Content-Range"))
                .flatMap(x -> x.stream().findFirst())
                .map(HeaderRange::of);
    }
}
