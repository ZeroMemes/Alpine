package me.zero.alpine.listener;

/**
 * The body of a listener, called when an event is invoked.
 * It is important to note that method reference implementations
 * of this functional interface will result in incorrect target
 * assigning, and the hook (most likely) will not be invoked.
 *
 * @param <T> Target event type
 *
 * @author Brady
 * @since 1/22/2017 12:00 PM
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
