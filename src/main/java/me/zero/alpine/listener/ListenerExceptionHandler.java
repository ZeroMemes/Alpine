package me.zero.alpine.listener;

import me.zero.alpine.bus.EventBusBuilder;

/**
 * @author Brady
 * @since 3.0.0
 */
@FunctionalInterface
public interface ListenerExceptionHandler {

    /**
     * Called when an exception is thrown by a Listener upon event dispatch. If {@code true} is returned from this
     * function, then the exception is propagated upwards and may be handled by another part of the application. If
     * {@code false} is returned, the exception is completely ignored, which can have unintended side effects that
     * result in hard-to-debug errors.
     *
     * @param event    The event
     * @param listener The listener that threw an exception
     * @param cause    The exception that was thrown
     * @param <T>      The event type
     * @return Whether to propagate the exception upwards
     */
    <T> boolean handleException(T event, Listener<T> listener, Throwable cause);

    /**
     * Default implementation of {@link ListenerExceptionHandler} used by {@link EventBusBuilder}. Prints a simple
     * message indicating that an event Listener threw an exception, along with the exception stacktrace, and then
     * permits the exception to propagate upwards.
     */
    ListenerExceptionHandler DEFAULT = new ListenerExceptionHandler() {

        @Override
        public <T> boolean handleException(T event, Listener<T> listener, Throwable cause) {
            System.err.println("An exception was thrown by a Listener while dispatching an Event" +
                "\n\tEvent:    " + event +
                "\n\tType:     " + event.getClass() +
                "\n\tListener: " + listener +
                "\n\tTarget:   " + listener.getTarget()
            );
            cause.printStackTrace();
            return true;
        }
    };
}
