package me.zero.alpine.listener;

/**
 * @author Brady
 * @since 3.0.0
 */
enum DefaultListenerExceptionHandler implements ListenerExceptionHandler {
    INSTANCE;

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
}
