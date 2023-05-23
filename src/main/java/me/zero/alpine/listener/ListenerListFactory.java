package me.zero.alpine.listener;

import me.zero.alpine.bus.EventBusBuilder;
import me.zero.alpine.listener.concurrent.CopyOnWriteListenerList;

/**
 * @author Brady
 * @since 3.0.0
 */
@FunctionalInterface
public interface ListenerListFactory {

    <T> ListenerList<T> create(Class<T> cls);

    /**
     * Default implementation of {@link ListenerListFactory} used by {@link EventBusBuilder}. Returns a new instance of
     * {@link CopyOnWriteListenerList} upon each invocation.
     */
    ListenerListFactory DEFAULT = new ListenerListFactory() {

        @Override
        public <T> ListenerList<T> create(Class<T> cls) {
            return new CopyOnWriteListenerList<>();
        }
    };
}
