package fr.ouestfrance.querydsl.postgrest.model.exceptions;

/**
 * Exception for missing configuration
 */
public class MissingConfigurationException extends RuntimeException {
    /**
     * Constructor
     * @param clazz misconfigured class
     * @param message cause of misconfiguration
     */
    public MissingConfigurationException(Class<?> clazz, String message) {
        super("Missing configuration for class " + clazz.getName() + " cause :" + message);
    }

}
