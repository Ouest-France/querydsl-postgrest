package fr.ouestfrance.querydsl.postgrest;

import fr.ouestfrance.querydsl.postgrest.model.exceptions.PostgrestRequestException;
import org.springframework.http.HttpStatusCode;
import org.springframework.util.ClassUtils;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.Optional;

/**
 * Default {@link ExceptionHandler} : a 404 is translated to a {@link PostgrestRequestException},
 * every other exception is kept as is.
 * Works with RestClient and, when spring-webflux is on the classpath, with WebClient.
 */
public class HttpStatusExceptionHandler implements ExceptionHandler {

    private static final boolean WEBCLIENT_PRESENT = ClassUtils.isPresent(
            "org.springframework.web.reactive.function.client.WebClientResponseException",
            HttpStatusExceptionHandler.class.getClassLoader());

    @Override
    public RuntimeException handle(String resource, RuntimeException exception) {
        boolean notFound = statusOf(exception).filter(HttpStatusCode::is4xxClientError)
                .map(status -> status.isSameCodeAs(HttpStatusCode.valueOf(404)))
                .orElse(false);
        if (notFound) {
            return new PostgrestRequestException(resource, exception.getMessage(), exception, bodyOf(exception));
        }
        return exception;
    }

    private static Optional<HttpStatusCode> statusOf(RuntimeException exception) {
        if (exception instanceof RestClientResponseException restException) {
            return Optional.of(restException.getStatusCode());
        }
        if (WEBCLIENT_PRESENT && exception instanceof WebClientResponseException webException) {
            return Optional.of(webException.getStatusCode());
        }
        return Optional.empty();
    }

    private static String bodyOf(RuntimeException exception) {
        if (exception instanceof RestClientResponseException restException) {
            return restException.getResponseBodyAsString();
        }
        if (WEBCLIENT_PRESENT && exception instanceof WebClientResponseException webException) {
            return webException.getResponseBodyAsString();
        }
        return null;
    }
}
