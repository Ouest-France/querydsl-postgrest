package fr.ouestfrance.querydsl.postgrest.model.exceptions;

import lombok.Getter;
import lombok.Setter;

/**
 * Runtime exception for querying failure
 */
@Getter
@Setter
public class PostgrestRequestException extends RuntimeException {

    private String responseBody;

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
     * @param resourceName resource name
     * @param message cause message
     * @param cause exception raised
     * @param errorBody error body
     */
    public PostgrestRequestException(String resourceName, String message, Throwable cause, String errorBody) {
        this("Error on querying " + resourceName + " cause by " + message, cause);
        this.responseBody = errorBody;
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
