package me.zero.alpine.listener;

import me.zero.alpine.listener.concurrent.CopyOnWriteListenerList;
import org.jetbrains.annotations.NotNull;

/**
 * @author Brady
 * @since 3.0.0
 */
public enum DefaultListenerListFactory implements ListenerListFactory {
    INSTANCE;

    @Override
    public @NotNull <T> ListenerList<T> create(Class<T> eventType) {
        return new CopyOnWriteListenerList<>();
    }
}
