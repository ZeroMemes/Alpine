package me.zero.alpine.event;

import me.zero.alpine.exception.EventTypeException;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;

/**
 * @author Brady
 * @since 3.0.0
 */
public final class Events {

    private Events() {}

    /**
     * @param type An event type
     * @return The validated event class
     * @throws EventTypeException If the type is invalid
     * @since 3.0.0
     */
    public static <T> Class<T> validateEventType(Type type) {
        if (type instanceof TypeVariable) {
            throw new EventTypeException("Listener target cannot be a type variable");
        }
        if (type instanceof ParameterizedType) {
            throw new EventTypeException("Listener target cannot be a generic type");
        }
        if (!(type instanceof Class<?>)) {
            throw new EventTypeException("Unable to resolve Listener target class (Unrecognized " + type.getClass() + ")");
        }

        // If the target type actually is a class, make sure that it doesn't have any type
        // parameters that were omitted, avoiding the other checks.
        // noinspection unchecked
        Class<T> target = (Class<T>) type;
        if (target.getTypeParameters().length != 0) {
            throw new EventTypeException("Listener target cannot be a generic type");
        }
        return target;
    }
}
