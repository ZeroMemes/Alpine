package me.zero.alpine.exception;

import me.zero.alpine.event.Events;

/**
 * Thrown by {@link Events#validateEventType} when the specified type is not a valid event type.
 *
 * @author Brady
 * @since 3.0.0
 */
public class EventTypeException extends RuntimeException {

    public EventTypeException(String message) {
        super(message);
    }
}
