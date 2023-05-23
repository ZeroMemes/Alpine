package me.zero.alpine.event.dispatch;

import me.zero.alpine.listener.Listener;
import me.zero.alpine.listener.ListenerExceptionHandler;

import java.util.Objects;

/**
 * @author Brady
 * @since 3.0.0
 */
@FunctionalInterface
public interface EventDispatcher {

    <T> void dispatch(T event, Listener<T>[] listeners);

    static EventDispatcher fastEventDispatcher() {
        return FastEventDispatcher.INSTANCE;
    }

    static EventDispatcher withExceptionHandler(ListenerExceptionHandler exceptionHandler) {
        Objects.requireNonNull(exceptionHandler);
        return new ExceptionHandlingDispatcher(exceptionHandler);
    }
}

