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
     * @return A new listener list
     * @param <T> The event type
     */
    <T> @NotNull ListenerList<T> create(Class<T> eventType);

    /**
     * Default implementation of {@link ListenerListFactory} used by {@link EventBusBuilder}. Returns a new instance of
     * {@link CopyOnWriteListenerList} upon each invocation.
     */
    ListenerListFactory DEFAULT = new ListenerListFactory() {

        @Override
        public <T> @NotNull ListenerList<T> create(Class<T> cls) {
            return new CopyOnWriteListenerList<>();
        }
    };
}
