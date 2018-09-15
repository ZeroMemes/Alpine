package me.zero.alpine.bus.type;

import me.zero.alpine.bus.EventBus;

/**
 * A type of {@link EventBus} that can have child event buses
 * "attached" to it, allowing them to receive the same calls
 * as the "parent" event bus.
 *
 * @author Brady
 * @since 9/15/2018
 */
public interface AttachableEventBus extends EventBus {

    /**
     * Attaches another event bus onto this event bus.
     * Simple way of hooking in external event buses.
     * In applications with a central EventBus, this allows
     * users making extensions to applications to use
     * their own event bus, as long as it implements
     * {@link EventBus}. Actions being carried out should
     * prioritize the parent EventBus.
     *
     * @param bus Other EventBus
     * @see AttachableEventBus#detach(EventBus)
     */
    void attach(EventBus bus);

    /**
     * Detaches another event bus that has already
     * been attached to this event bus.
     *
     * @param bus Other EventBus
     * @see AttachableEventBus#attach(EventBus)
     */
    void detach(EventBus bus);
}
