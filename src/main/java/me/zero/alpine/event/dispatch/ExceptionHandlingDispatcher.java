package me.zero.alpine.event.dispatch;

import me.zero.alpine.listener.Listener;
import me.zero.alpine.listener.ListenerExceptionHandler;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;

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
    public <T> void dispatch(final @NotNull T event, final @NotNull Iterator<Listener<T>> listeners) {
        Listener<T> last = null;
        try {
            while (listeners.hasNext()) {
                (last = listeners.next()).accept(event);
            }
        } catch (Throwable cause) {
            if (this.exceptionHandler.handleException(event, last, cause)) {
                throw cause;
            }
        }
    }
}
