package fr.ouestfrance.querydsl.postgrest;

import fr.ouestfrance.querydsl.postgrest.model.exceptions.PostgrestRequestException;
import org.springframework.http.HttpStatusCode;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.Optional;

/**
 * Default {@link ExceptionHandler} : a 404 is translated to a {@link PostgrestRequestException},
 * every other exception is kept as is.
 * The status and the body are read by reflection on {@code getStatusCode()} and {@code getResponseBodyAsString()},
 * so it works with any client exception exposing them (RestClient, WebClient, ...) without depending on its classes.
 */
public class HttpStatusExceptionHandler implements ExceptionHandler {

    private static final String STATUS_METHOD = "getStatusCode";
    private static final String BODY_METHOD = "getResponseBodyAsString";

    @Override
    public RuntimeException handle(String resource, RuntimeException exception) {
        boolean notFound = invoke(exception, STATUS_METHOD, HttpStatusCode.class)
                .map(status -> status.isSameCodeAs(HttpStatusCode.valueOf(404)))
                .orElse(false);
        if (notFound) {
            String body = invoke(exception, BODY_METHOD, String.class).orElse(null);
            return new PostgrestRequestException(resource, exception.getMessage(), exception, body);
        }
        return exception;
    }

    private static <T> Optional<T> invoke(Object target, String methodName, Class<T> type) {
        Method method = ReflectionUtils.findMethod(target.getClass(), methodName);
        if (method == null || !type.isAssignableFrom(method.getReturnType())) {
            return Optional.empty();
        }
        ReflectionUtils.makeAccessible(method);
        return Optional.ofNullable(type.cast(ReflectionUtils.invokeMethod(method, target)));
    }
}
