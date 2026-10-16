package fr.ouestfrance.querydsl.postgrest;

import fr.ouestfrance.querydsl.postgrest.model.BulkResponse;
import fr.ouestfrance.querydsl.postgrest.model.HeaderRange;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpHeaders;

import java.util.List;
import java.util.Optional;

/**
 * Utility class that helps to transform response
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ResponseUtils {

    /**
     * Transform a body and its headers to bulkResponse
     *
     * @param body    body
     * @param headers response headers
     * @param <T>     Type of response
     * @return BulkResponse
     */
    public static <T> BulkResponse<T> toBulkResponse(List<T> body, HttpHeaders headers) {
        Optional<HeaderRange> count = getCount(headers);
        return new BulkResponse<>(body, count.map(HeaderRange::getCount).orElse(0L),
                count.map(HeaderRange::getTotalElements).orElse(0L));
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
