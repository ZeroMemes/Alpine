package me.zero.alpine.exception;

import me.zero.alpine.listener.Listener;
import me.zero.alpine.listener.Subscribe;

/**
 * Thrown when a {@link Listener} field annotated with {@link Subscribe} is valid for discovery, but has an invalid
 * generic signature.
 *
 * @author Brady
 * @since 3.0.0
 */
public final class ListenerFieldException extends ListenerDiscoveryException {

    public ListenerFieldException(String message) {
        super(message);
    }
}
