package me.zero.alpine.event.dispatch;

import me.zero.alpine.listener.Listener;

/**
 * @author Brady
 * @since 3.0.0
 */
enum FastEventDispatcher implements EventDispatcher {
    INSTANCE;

    @Override
    public <T> void dispatch(final T event, final Listener<T>[] listeners) {
        int i = 0;
        while (i != listeners.length) {
            listeners[i].accept(event);
            i++;
        }
    }
}
