package me.zero.alpine.bus;

/**
 * A type of {@link EventBus} that can have child event buses "attached" to it, allowing them to receive the same method
 * calls as the "parent" event bus.
 *
 * @author Brady
 * @since 9/15/2018
 */
public interface AttachableEventBus extends EventBus {

    /**
     * Attaches another {@link EventBus} onto this event bus, allowing it to receive the same method calls as this bus.
     * In applications with a central {@link EventBus}, this allows users making extensions to applications to use their
     * own event bus. Method calls being carried out should prioritize the parent {@link EventBus}.
     *
     * @param bus The bus
     * @see AttachableEventBus#detach(EventBus)
     */
    void attach(EventBus bus);

    /**
     * Detaches the specified {@link EventBus} from this bus. Has no effect if {@link #attach(EventBus)} has not
     * already been called on the given bus.
     *
     * @param bus The bus
     * @see AttachableEventBus#attach(EventBus)
     */
    void detach(EventBus bus);
}
