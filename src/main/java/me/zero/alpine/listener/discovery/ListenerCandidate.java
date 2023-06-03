package me.zero.alpine.listener.discovery;

import me.zero.alpine.exception.ListenerBindException;
import me.zero.alpine.listener.Listener;
import me.zero.alpine.listener.Subscriber;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * @author Brady
 * @since 3.0.0
 */
@FunctionalInterface
public interface ListenerCandidate<T> {

    /**
     * Attempts to bind the specified {@link Subscriber} instance to the Listener(s) provided by its class definition.
     * If this operation fails for any reason, then a {@link ListenerBindException} may be thrown.
     *
     * @param instance A subscriber instance
     * @return The bound Listener
     * @throws ListenerBindException If binding to the candidate fails
     * @since 3.0.0
     */
    Stream<Listener<T>> bind(Subscriber instance);

    /**
     * Wraps the specified bind function, which produces a single {@link Listener} instance, and creates a new
     * {@link ListenerCandidate}. This helper method can be used to avoid having to call {@link Stream#of} in every
     * candidate implementation which only produces a single instance.
     *
     * @param function The bind function
     * @param <T>      The event type
     * @return The created candidate
     * @since 3.0.0
     */
    static <T> @NotNull ListenerCandidate<T> single(@NotNull Function<Subscriber, Listener<T>> function) {
        Objects.requireNonNull(function);
        return instance -> Stream.of(function.apply(instance));
    }
}
