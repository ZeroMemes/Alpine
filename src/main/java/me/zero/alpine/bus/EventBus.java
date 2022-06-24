package me.zero.alpine.bus;

import me.zero.alpine.event.Cancellable;
import me.zero.alpine.listener.EventSubscriber;
import me.zero.alpine.listener.Listener;

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
     * Discovers all the valid Listener instances defined by the specified {@link EventSubscriber} and adds them to the bus.
     *
     * @param subscriber The subscriber to be added
     */
    void subscribe(EventSubscriber subscriber);

    /**
     * Subscribes an individual listener object.
     *
     * @param listener The individual listener to subscribe
     */
    void subscribe(Listener<?> listener);

    /**
     * @param subscribers An array of subscribers
     * @see #subscribe(EventSubscriber)
     */
    default void subscribeAll(EventSubscriber... subscribers) {
        for (EventSubscriber subscriber : subscribers) {
            this.subscribe(subscriber);
        }
    }

    /**
     * @param subscribers An iterable of subscribers
     * @see #subscribe(EventSubscriber)
     */
    default void subscribeAll(Iterable<EventSubscriber> subscribers) {
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
     * Removes all the previously identified Listener instances defined by the specified {@link EventSubscriber}.
     *
     * @param subscriber The subscriber to be unsubscribed from the event bus
     */
    void unsubscribe(EventSubscriber subscriber);

    /**
     * Unsubscribe an individual listener object.
     *
     * @param listener The individual listener to unsubscribe
     */
    void unsubscribe(Listener<?> listener);

    /**
     * @param subscribers An array of subscribers
     * @see #unsubscribe(EventSubscriber)
     */
    default void unsubscribeAll(EventSubscriber... subscribers) {
        for (EventSubscriber subscriber : subscribers) {
            this.unsubscribe(subscriber);
        }
    }

    /**
     * @param subscribers An iterable of subscribers
     * @see #unsubscribe(EventSubscriber)
     */
    default void unsubscribeAll(Iterable<EventSubscriber> subscribers) {
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
     */
    void post(Object event);

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
