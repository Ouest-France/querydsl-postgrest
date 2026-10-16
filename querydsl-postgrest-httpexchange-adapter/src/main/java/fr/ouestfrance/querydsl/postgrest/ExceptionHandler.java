package fr.ouestfrance.querydsl.postgrest;

/**
 * Translate an exception raised by the http client into the exception thrown to the caller
 */
@FunctionalInterface
public interface ExceptionHandler {

    /**
     * Handle an exception
     *
     * @param resource  resource (or rpc name) being called
     * @param exception exception raised by the http client
     * @return exception to throw
     */
    RuntimeException handle(String resource, RuntimeException exception);
}
