package me.zero.alpine.listener;

import me.zero.alpine.event.dispatch.EventDispatcher;
import me.zero.alpine.listener.concurrent.ReadWriteLockListenerList;
import me.zero.alpine.listener.concurrent.SynchronizedListenerList;
import org.jetbrains.annotations.NotNull;

/**
 * @author Brady
 * @since 3.0.0
 */
public interface ListenerList<T> {

    void post(@NotNull T event, @NotNull EventDispatcher dispatcher);

    boolean add(@NotNull Listener<T> listener);

    boolean remove(@NotNull Listener<T> listener);

    static <T> ListenerList<T> synchronize(@NotNull ListenerList<T> list) {
        return new SynchronizedListenerList<>(list);
    }

    static <T> ListenerList<T> synchronize(@NotNull ListenerList<T> list, @NotNull Object sync) {
        return new SynchronizedListenerList<>(list, sync);
    }

    static <T> ListenerList<T> readWriteLock(@NotNull ListenerList<T> list) {
        return new ReadWriteLockListenerList<>(list);
    }
}
