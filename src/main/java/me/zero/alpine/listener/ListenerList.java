package me.zero.alpine.listener;

import me.zero.alpine.event.dispatch.EventDispatcher;

/**
 * @author Brady
 * @since 3.0.0
 */
public interface ListenerList<T> {

    void post(T event, EventDispatcher dispatcher);

    boolean add(Listener<T> listener);

    boolean remove(Listener<T> listener);
}
