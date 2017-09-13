package me.zero.alpine;

import me.zero.alpine.listener.Listener;

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
    void subscribe(Iterable<Object> objects);

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
    void unsubscribe(Iterable<Object> objects);

    /**
     * Posts an event to all registered {@code Listeners}.
     * Done via Reflection Method Invokation
     *
     * @see Listener#invoke(Object)
     *
     * @param event Event being called
     */
    void post(Object event);

    /**
     * Attaches another event bus onto this event bus.
     * Simple way of hooking in external event buses.
     * In applications with a central EventBus, this allows
     * users making extensions to applications to use
     * their own event bus, as long as it implements
     * {@code EventBus}. Actions being carried out should
     * prioritize the parent EventBus.
     *
     * @param bus Other EventBus
     * @see EventBus#detach(EventBus)
     */
    void attach(EventBus bus);

    /**
     * Detaches another event bus that has already
     * been attached to this event bus.
     *
     * @param bus Other EventBus
     * @see EventBus#attach(EventBus)
     */
    void detach(EventBus bus);
}
