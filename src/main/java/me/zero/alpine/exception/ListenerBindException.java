package me.zero.alpine.exception;

/**
 * @author Brady
 * @since 3.0.0
 */
public class ListenerBindException extends RuntimeException {

    public ListenerBindException() {}

    public ListenerBindException(String message) {
        super(message);
    }

    public ListenerBindException(String message, Throwable cause) {
        super(message, cause);
    }

    public ListenerBindException(Throwable cause) {
        super(cause);
    }
}
