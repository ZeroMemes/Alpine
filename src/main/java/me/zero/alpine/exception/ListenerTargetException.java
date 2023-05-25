package me.zero.alpine.exception;

import me.zero.alpine.listener.Listener;

/**
 * Thrown when a Listener's event target type is set using {@link Listener#setTarget}, but the new target type isn't
 * assignable to the Listener's current target type.
 *
 * @author Brady
 * @since 3.0.0
 */
public final class ListenerTargetException extends EventTypeException {

    public ListenerTargetException(String message) {
        super(message);
    }
}
