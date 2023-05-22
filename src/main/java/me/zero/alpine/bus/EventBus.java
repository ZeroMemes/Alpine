package me.zero.alpine.bus;

import me.zero.alpine.event.Cancellable;
import me.zero.alpine.listener.Listener;
import me.zero.alpine.listener.Subscriber;
import org.jetbrains.annotations.NotNull;

/**
 * The core of an event-driven application.
 * <p>
 * Used to dispatch events to {@link Listener}s, and manage the active {@link Listener}s.
 *
 * @author Brady
 * @since 1.2
 */
public interface EventBus {

    /**
     * @return The name of this event bus
     * @since 2.0.0
     */
    @NotNull String name();

    /**
     * Discovers all the valid Listener instances defined by the specified {@link Subscriber} and adds them to the bus.
     *
     * @param subscriber The subscriber to be added
     * @since 1.2
     */
    void subscribe(@NotNull Subscriber subscriber);

    /**
     * Subscribes an individual listener object.
     *
     * @param listener The individual listener to subscribe
     * @param <T> The target event type
     * @since 1.8
     */
    <T> void subscribe(@NotNull Listener<T> listener);

    /**
     * @param subscribers An array of subscribers
     * @see #subscribe(Subscriber)
     * @since 1.7
     */
    default void subscribeAll(@NotNull Subscriber... subscribers) {
        for (Subscriber subscriber : subscribers) {
            this.subscribe(subscriber);
        }
    }

    /**
     * @param subscribers An iterable of subscribers
     * @see #subscribe(Subscriber)
     * @since 1.7
     */
    default void subscribeAll(@NotNull Iterable<Subscriber> subscribers) {
        subscribers.forEach(this::subscribe);
    }

    /**
     * @param listeners An array of listeners
     * @see #subscribe(Listener)
     * @since 1.9
     */
    default void subscribeAll(@NotNull Listener<?>... listeners) {
        for (Listener<?> listener : listeners) {
            this.subscribe(listener);
        }
    }

    /**
     * Removes all the previously identified Listener instances defined by the specified {@link Subscriber}.
     *
     * @param subscriber The subscriber to be unsubscribed from the event bus
     * @since 1.2
     */
    void unsubscribe(@NotNull Subscriber subscriber);

    /**
     * Unsubscribe an individual listener object.
     *
     * @param listener The individual listener to unsubscribe
     * @param <T> The target event type
     * @since 1.8
     */
    <T> void unsubscribe(@NotNull Listener<T> listener);

    /**
     * @param subscribers An array of subscribers
     * @see #unsubscribe(Subscriber)
     * @since 1.7
     */
    default void unsubscribeAll(@NotNull Subscriber... subscribers) {
        for (Subscriber subscriber : subscribers) {
            this.unsubscribe(subscriber);
        }
    }

    /**
     * @param subscribers An iterable of subscribers
     * @see #unsubscribe(Subscriber)
     * @since 1.7
     */
    default void unsubscribeAll(@NotNull Iterable<Subscriber> subscribers) {
        subscribers.forEach(this::unsubscribe);
    }

    /**
     * @param listeners An array of listeners
     * @see #unsubscribe(Listener)
     * @since 1.9
     */
    default void unsubscribeAll(@NotNull Listener<?>... listeners) {
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
     * @since 1.2
     */
    <T> void post(@NotNull T event);

    /**
     * Posts a cancellable event and returns whether the event has been cancelled.
     *
     * @param event Event being called
     * @return Whether the event has been cancelled
     * @since 2.0.0
     */
    default boolean post(@NotNull Cancellable event) {
        this.post((Object) event);
        return event.isCancelled();
    }
}
