package me.zero.alpine.listener;

import me.zero.alpine.bus.EventBusBuilder;
import me.zero.alpine.event.Events;
import me.zero.alpine.listener.concurrent.CopyOnWriteListenerList;
import org.jetbrains.annotations.NotNull;

/**
 * @author Brady
 * @since 3.0.0
 */
@FunctionalInterface
public interface ListenerListFactory {

    /**
     * Creates a new {@link ListenerList} instance for the given event type. The implementation of this method is not
     * required to {@link Events#validateEventType validate} the specified event type, as that should be done by the
     * caller.
     *
     * @param eventType The event class
     * @param <T>       The event type
     * @return A new listener list
     */
    <T> @NotNull ListenerList<T> create(Class<T> eventType);

    /**
     * Returns the default implementation of {@link ListenerListFactory} used by {@link EventBusBuilder}, which creates
     * a new instance of {@link CopyOnWriteListenerList} upon each {@link ListenerListFactory#create} invocation.
     *
     * @return The default factory
     */
    static @NotNull ListenerListFactory defaultFactory() {
        return DefaultListenerListFactory.INSTANCE;
    }
}
