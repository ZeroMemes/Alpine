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
     * Validates the specified type as a valid event type. In order for a type to be considered valid, it must be a
     * {@link Class} that has no type parameters. This does not include raw types, which are still considered invalid.
     * If the type is valid, it is returned from this function as a {@link Class}; otherwise, an
     * {@link EventTypeException} is thrown with a descriptive error message.
     *
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

        // noinspection unchecked
        Class<T> target = (Class<T>) type;
        // Make sure that it doesn't have any type parameters that were omitted, avoiding the other checks.
        if (target.getTypeParameters().length != 0) {
            throw new EventTypeException("Listener target cannot be a generic type");
        }
        if (target.isPrimitive()) {
            throw new EventTypeException("Listener target cannot be a primitive");
        }
        if (target.isArray()) {
            throw new EventTypeException("Listener target cannot be an array type");
        }
        return target;
    }
}
