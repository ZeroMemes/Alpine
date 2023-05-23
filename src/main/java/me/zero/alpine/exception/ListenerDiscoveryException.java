package me.zero.alpine.exception;

/**
 * @author Brady
 * @since 3.0.0
 */
public class ListenerDiscoveryException extends RuntimeException {

    public ListenerDiscoveryException() {}

    public ListenerDiscoveryException(String message) {
        super(message);
    }

    public ListenerDiscoveryException(String message, Throwable cause) {
        super(message, cause);
    }

    public ListenerDiscoveryException(Throwable cause) {
        super(cause);
    }
}
