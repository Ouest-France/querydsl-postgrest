package fr.ouestfrance.querydsl.postgrest.model.exceptions;

/**
 * Runtime exception for querying failure
 */
public class PostgrestRequestException extends RuntimeException {

    /**
     * PostgrestRequestException constructor
     *
     * @param resourceName resource name
     * @param message      cause message
     */
    public PostgrestRequestException(String resourceName, String message) {
        this(resourceName, message, null);
    }


    /**
     * PostgrestRequestException constructor
     *
     * @param resourceName resource name
     * @param message      cause message
     * @param cause        exception raised
     */

    public PostgrestRequestException(String resourceName, String message, Throwable cause) {
        this("Error on querying " + resourceName + " cause by " + message, cause);
    }

    /**
     * PostgrestRequestException constructor
     *
     * @param message cause message
     */
    public PostgrestRequestException(String message) {
        super(message);
    }

    /**
     * PostgrestRequestException constructor
     *
     * @param message cause message
     * @param cause   exception raised
     */
    public PostgrestRequestException(String message, Throwable cause) {
        super(message, cause);
    }
}
