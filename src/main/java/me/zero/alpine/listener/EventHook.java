package me.zero.alpine.listener;

/**
 * The body of a listener, called when an event is posted and the managing {@link Listener}
 * is prepared to accept it. It is important to note that method reference implementations
 * of this functional interface will result in incorrect target assignment, and the hook
 * (most likely) will not be invoked.
 *
 * @param <T> Target event type
 *
 * @author Brady
 * @since 1/22/2017
 */
@FunctionalInterface
public interface EventHook<T> {

    /**
     * Invokes this Listener with the event
     *
     * @param event The Event being Invoked
     */
    void invoke(T event);
}
