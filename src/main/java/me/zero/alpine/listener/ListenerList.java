package me.zero.alpine.listener;

import me.zero.alpine.event.dispatch.EventDispatcher;
import me.zero.alpine.listener.concurrent.ReadWriteLockListenerList;
import me.zero.alpine.listener.concurrent.SynchronizedListenerList;

/**
 * @author Brady
 * @since 3.0.0
 */
public interface ListenerList<T> {

    void post(T event, EventDispatcher dispatcher);

    boolean add(Listener<T> listener);

    boolean remove(Listener<T> listener);

    static <T> ListenerList<T> synchronize(ListenerList<T> list) {
        return new SynchronizedListenerList<>(list);
    }

    static <T> ListenerList<T> synchronize(ListenerList<T> list, Object sync) {
        return new SynchronizedListenerList<>(list, sync);
    }

    static <T> ListenerList<T> readWriteLock(ListenerList<T> list) {
        return new ReadWriteLockListenerList<>(list);
    }
}
