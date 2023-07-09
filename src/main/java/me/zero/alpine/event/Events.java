package me.zero.alpine.event;

import me.zero.alpine.exception.EventTypeException;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.Objects;

/**
 * Utility methods relating to Events.
 *
 * @author Brady
 * @since 3.0.0
 */
public final class Events {

    private Events() {}

    /**
     * Validates the specified type as a valid event type. In order for a type to be considered valid, it must be a
     * {@link Class} and pass the checks imposed by {@link #validateEventType(Class)}.
     *
     * @param type An event type
     * @return The validated event class
     * @throws EventTypeException   If the type is invalid
     * @throws NullPointerException If the type is {@code null}
     * @since 3.0.0
     */
    public static Class<?> validateEventType(@NotNull Type type) {
        Objects.requireNonNull(type);
        if (type instanceof TypeVariable) {
            throw new EventTypeException("Listener target cannot be a type variable");
        }
        if (type instanceof ParameterizedType) {
            final ParameterizedType parameterized = (ParameterizedType) type;
            for (Type arg : parameterized.getActualTypeArguments()) {
                if (!(arg instanceof WildcardType)) {
                    throw new EventTypeException("Generic targets can only contain wildcards");
                }
            }
            return validateEventType((Class<?>) parameterized.getRawType());
        }
        if (!(type instanceof Class)) {
            throw new EventTypeException("Unable to resolve Listener target class (Unrecognized " + type.getClass() + ")");
        }
        return validateEventType((Class<?>) type);
    }

    /**
     * Validates the specified class as a valid event type. In order for a type to be considered valid, it must be a
     * {@link Class} that has no type parameters. This does not include raw types, which are still considered invalid.
     * If the type is valid, it is returned back from this method; otherwise, an {@link EventTypeException} is thrown
     * with a descriptive error message.
     *
     * @param type An event class
     * @param <T>  The class type
     * @return The validated event class
     * @throws EventTypeException   If the class is invalid
     * @throws NullPointerException If the class is {@code null}
     * @since 3.0.0
     */
    public static <T> Class<T> validateEventType(@NotNull Class<T> type) {
        Objects.requireNonNull(type);
        if (type.isPrimitive()) {
            throw new EventTypeException("Listener target cannot be a primitive");
        }
        if (type.isArray()) {
            throw new EventTypeException("Listener target cannot be an array type");
        }
        return type;
    }
}
