package me.zero.alpine;

import me.zero.alpine.listener.Listener;

import java.util.List;

/**
 * An Event Bus is used to manage the flow of events.
 * Objects can be subscribed to the EventBus to be used
 * as listeners for events.
 *
 * @author Brady
 * @since 5/27/2017 9:53 AM
 */
public interface EventBus {

    /**
     * Discovers all valid listeners from the Object
     * specified and then registers them in the
     * form of {@code Listeners}
     *
     * @see Listener
     *
     * @param object The object containing possible Event Listeners
     */
    void subscribe(Object object);

    /**
     * Subscribes all elements from an array
     *
     * @see Listener
     * @see #subscribe(Object)
     *
     * @param objects The array of objects
     */
    void subscribe(Object... objects);

    /**
     * Subscribes all elements from a list
     *
     * @see Listener
     * @see #subscribe(Object)
     *
     * @param objects The list of objects
     */
    void subscribe(List<Object> objects);

    /**
     * Unsubscribes an object from the listener map
     *
     * @see #subscribe(Object)
     *
     * @param object The object being unsubscribed
     */
    void unsubscribe(Object object);

    /**
     * Unsubscribes all elements from an array
     *
     * @see Listener
     * @see #unsubscribe(Object)
     *
     * @param objects The array of objects
     */
    void unsubscribe(Object... objects);

    /**
     * Unsubscribes all elements from a list
     *
     * @see Listener
     * @see #unsubscribe(Object)
     *
     * @param objects The list of objects
     */
    void unsubscribe(List<Object> objects);

    /**
     * Posts an event to all registered {@code Listeners}.
     * Done via Reflection Method Invokation
     *
     * @see Listener#invoke(Object)
     *
     * @param event Event being called
     */
    void post(Object event);
}
