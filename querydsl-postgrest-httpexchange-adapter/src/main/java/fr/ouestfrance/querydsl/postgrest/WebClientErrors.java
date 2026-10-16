package fr.ouestfrance.querydsl.postgrest;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.web.reactive.function.client.WebClientResponseException;

/**
 * Isolates WebClient classes so they are only loaded when spring-webflux is on the classpath
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
final class WebClientErrors {

    static boolean isNotFound(RuntimeException exception) {
        return exception instanceof WebClientResponseException.NotFound;
    }

    static String responseBody(RuntimeException exception) {
        return exception instanceof WebClientResponseException e ? e.getResponseBodyAsString() : null;
    }
}
