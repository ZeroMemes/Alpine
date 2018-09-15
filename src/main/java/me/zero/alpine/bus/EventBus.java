package me.zero.alpine.bus;

import me.zero.alpine.listener.Listenable;
import me.zero.alpine.listener.Listener;

/**
 * An Event Bus is used to manage the flow of events. Listenables and
 * individual/standalone Listeners may be subscribed/unsubscribed from
 * the event bus to listen to events.
 *
 * @author Brady
 * @since 5/27/2017 9:53 AM
 */
public interface EventBus {

    /**
     * Discovers all of the valid Listeners defined in the
     * specified Listenable and subscribes them to the event bus.
     *
     * @see Listener
     *
     * @param listenable The Listenable to be subscribed to the event bus
     */
    void subscribe(Listenable listenable);

    /**
     * Subscribes an individual listener object, as opposed to subscribing
     * all of the listener fields that are defined in a class.
     *
     * @see Listener
     *
     * @param listener The individual listener to subscribe
     */
    void subscribe(Listener listener);

    /**
     * Subscribes all of the specified Listenables
     *
     * @see Listener
     * @see #subscribe(Listenable)
     *
     * @param listenables An array of Listenable objects
     */
    void subscribeAll(Listenable... listenables);

    /**
     * Subscribes all of the specified Listenables
     *
     * @see Listener
     * @see #subscribe(Listenable)
     *
     * @param listenables An iterable of Listenable objects
     */
    void subscribeAll(Iterable<Listenable> listenables);

    /**
     * Unsubscribes all of the Listeners that are defined by the Listenable
     *
     * @see #subscribe(Listenable)
     *
     * @param listenable The Listenable to be unsubscribed from the event bus
     */
    void unsubscribe(Listenable listenable);

    /**
     * Unsubscribe an individual listener object.
     *
     * @see Listener
     *
     * @param listener The individual listener to unsubscribe
     */
    void unsubscribe(Listener listener);

    /**
     * Unsubscribes all of the specified Listenables
     *
     * @see Listener
     * @see #unsubscribe(Listenable)
     *
     * @param listenables The array of objects
     */
    void unsubscribeAll(Listenable... listenables);

    /**
     * Unsubscribes all of the specified Listenables
     *
     * @see Listener
     * @see #unsubscribe(Listenable)
     *
     * @param listenables The list of objects
     */
    void unsubscribeAll(Iterable<Listenable> listenables);

    /**
     * Posts an event to all registered {@code Listeners}.
     *
     * @see Listener#invoke(Object)
     *
     * @param event Event being called
     */
    void post(Object event);
}
