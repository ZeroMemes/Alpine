package me.zero.alpine.event.dispatch;

import me.zero.alpine.listener.Listener;
import me.zero.alpine.listener.ListenerExceptionHandler;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * @author Brady
 * @since 3.0.0
 */
@FunctionalInterface
public interface EventDispatcher {

    <T> void dispatch(@NotNull T event, @NotNull Listener<T>[] listeners);

    /**
     * Returns an optimized implementation of {@link EventDispatcher} which has no exception handling.
     *
     * @return The dispatcher
     * @since 3.0.0
     */
    static EventDispatcher fastEventDispatcher() {
        return FastEventDispatcher.INSTANCE;
    }

    /**
     * Returns a new {@link EventDispatcher} which invokes the specified {@link ListenerExceptionHandler} when a
     * Listener callback throws an exception. When an exception is thrown, regardless of the exception handler's
     * outcome (whether to propagate the exception upwards), none of the remaining Listeners will receive the current
     * event.
     *
     * @param exceptionHandler The exception handler
     * @return The dispatcher
     * @since 3.0.0
     */
    static EventDispatcher withExceptionHandler(@NotNull ListenerExceptionHandler exceptionHandler) {
        return new ExceptionHandlingDispatcher(Objects.requireNonNull(exceptionHandler));
    }
}

