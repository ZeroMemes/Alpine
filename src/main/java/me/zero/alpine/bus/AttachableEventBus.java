package me.zero.alpine.bus;

import org.jetbrains.annotations.NotNull;

/**
 * A type of {@link EventBus} that can have child event buses "attached" to it, allowing them to receive the same events
 * that get posted to the parent. In applications with a central {@link EventBus}, this allows users making extensions
 * to applications to use their own event bus(es).
 *
 * @author Brady
 * @since 1.8
 */
public interface AttachableEventBus extends EventBus {

    /**
     * Attaches another {@link EventBus} onto this event bus, allowing it to receive the events posted to this bus. Has
     * no effect if the given bus has already been attached to this bus.
     *
     * @param bus The bus
     * @return {@code true} if the bus was attached
     * @since 1.8
     */
    boolean attach(@NotNull EventBus bus);

    /**
     * Detaches the specified {@link EventBus} from this bus. Has no effect if {@link #attach(EventBus)} has not
     * already been called on the given bus.
     *
     * @param bus The bus
     * @return {@code true} if the bus was detached
     * @since 1.8
     */
    boolean detach(@NotNull EventBus bus);
}
