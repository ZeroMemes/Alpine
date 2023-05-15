package me.zero.alpine.bus;

import me.zero.alpine.event.Cancellable;
import me.zero.alpine.listener.Listener;
import me.zero.alpine.listener.Subscriber;

/**
 * The core of an event-driven application.
 * <p>
 * Used to dispatch events to {@link Listener}s, and manage the active {@link Listener}s.
 *
 * @author Brady
 * @since 5/27/2017
 */
public interface EventBus {

    String name();

    /**
     * Discovers all the valid Listener instances defined by the specified {@link Subscriber} and adds them to the bus.
     *
     * @param subscriber The subscriber to be added
     */
    void subscribe(Subscriber subscriber);

    /**
     * Subscribes an individual listener object.
     *
     * @param listener The individual listener to subscribe
     * @param <T> The target event type
     */
    <T> void subscribe(Listener<T> listener);

    /**
     * @param subscribers An array of subscribers
     * @see #subscribe(Subscriber)
     */
    default void subscribeAll(Subscriber... subscribers) {
        for (Subscriber subscriber : subscribers) {
            this.subscribe(subscriber);
        }
    }

    /**
     * @param subscribers An iterable of subscribers
     * @see #subscribe(Subscriber)
     */
    default void subscribeAll(Iterable<Subscriber> subscribers) {
        subscribers.forEach(this::subscribe);
    }

    /**
     * @param listeners An array of listeners
     * @see #subscribe(Listener)
     */
    default void subscribeAll(Listener<?>... listeners) {
        for (Listener<?> listener : listeners) {
            this.subscribe(listener);
        }
    }

    /**
     * Removes all the previously identified Listener instances defined by the specified {@link Subscriber}.
     *
     * @param subscriber The subscriber to be unsubscribed from the event bus
     */
    void unsubscribe(Subscriber subscriber);

    /**
     * Unsubscribe an individual listener object.
     *
     * @param listener The individual listener to unsubscribe
     * @param <T> The target event type
     */
    <T> void unsubscribe(Listener<T> listener);

    /**
     * @param subscribers An array of subscribers
     * @see #unsubscribe(Subscriber)
     */
    default void unsubscribeAll(Subscriber... subscribers) {
        for (Subscriber subscriber : subscribers) {
            this.unsubscribe(subscriber);
        }
    }

    /**
     * @param subscribers An iterable of subscribers
     * @see #unsubscribe(Subscriber)
     */
    default void unsubscribeAll(Iterable<Subscriber> subscribers) {
        subscribers.forEach(this::unsubscribe);
    }

    /**
     * @param listeners An array of listeners
     * @see #unsubscribe(Listener)
     */
    default void unsubscribeAll(Listener<?>... listeners) {
        for (Listener<?> listener : listeners) {
            this.unsubscribe(listener);
        }
    }

    /**
     * Posts an event to all registered {@link Listener}s.
     *
     * @param event Event being called
     * @see Listener#accept(Object)
     * @param <T> The event type
     */
    <T> void post(T event);

    /**
     * Posts a cancellable event and returns whether the event has been cancelled.
     *
     * @param event Event being called
     * @return Whether the event has been cancelled
     */
    default boolean post(Cancellable event) {
        this.post((Object) event);
        return event.isCancelled();
    }
}
