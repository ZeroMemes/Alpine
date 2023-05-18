package me.zero.alpine.event.dispatch;

import me.zero.alpine.listener.Listener;
import me.zero.alpine.listener.ListenerExceptionHandler;

/**
 * @author Brady
 * @since 3.0.0
 */
final class ExceptionHandlingDispatcher implements EventDispatcher {

    private final ListenerExceptionHandler exceptionHandler;

    public ExceptionHandlingDispatcher(ListenerExceptionHandler exceptionHandler) {
        this.exceptionHandler = exceptionHandler;
    }

    @Override
    public <T> void dispatch(final T event, final Listener<T>[] listeners) {
        int i = 0;
        try {
            while (i != listeners.length) {
                listeners[i].accept(event);
                i++;
            }
        } catch (Throwable cause) {
            if (this.exceptionHandler.handleException(event, listeners[i], cause)) {
                throw cause;
            }
        }
    }
}
